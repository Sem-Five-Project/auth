-- CREATE TABLE monthly_class_participants (
--     participant_id BIGSERIAL PRIMARY KEY,
--     student_id BIGINT NOT NULL,
--     class_id BIGINT NOT NULL,
--     payment_id BIGINT NOT NULL,
--     month INT NOT NULL,  -- 1 to 12
--     year INT NOT NULL,
--     created_at TIMESTAMP DEFAULT NOW(),
--     CONSTRAINT fk_student FOREIGN KEY (student_id) REFERENCES student_profile(student_id),
--     CONSTRAINT fk_class FOREIGN KEY (class_id) REFERENCES monthly_class(monthly_class_id),
--     CONSTRAINT fk_payment FOREIGN KEY (payment_id) REFERENCES payment(payment_id),
--     UNIQUE (student_id, class_id, month, year) -- avoid duplicates
-- );


-- Ensure unique (availability_id, slot_date), idempotent
DO $$
BEGIN
  IF NOT EXISTS (
    SELECT 1
    FROM pg_constraint
    WHERE conname = 'slot_instance_availability_date_uniq'
  ) THEN
    ALTER TABLE public.slot_instance
      ADD CONSTRAINT slot_instance_availability_date_uniq
      UNIQUE (availability_id, slot_date);
  END IF;
END$$;

-- Helpful index for lookups (idempotent)
CREATE INDEX IF NOT EXISTS idx_slot_instance_availability_id
  ON public.slot_instance (availability_id);

-- Optional: default slot status
DO $$
BEGIN
  IF NOT EXISTS (
    SELECT 1
    FROM information_schema.columns
    WHERE table_schema = 'public'
      AND table_name = 'slot_instance'
      AND column_name = 'status'
      AND column_default IS NOT NULL
  ) THEN
    ALTER TABLE public.slot_instance
      ALTER COLUMN status SET DEFAULT 'AVAILABLE';
  END IF;
END$$;


-- Map uppercase day names to Postgres DOW (0=Sunday..6=Saturday)
CREATE OR REPLACE FUNCTION public.day_name_to_dow(day_name text)
RETURNS int
LANGUAGE plpgsql IMMUTABLE AS $$
DECLARE
  k text := upper(trim(day_name));
BEGIN
  IF k = 'SUNDAY' THEN RETURN 0; END IF;
  IF k = 'MONDAY' THEN RETURN 1; END IF;
  IF k = 'TUESDAY' THEN RETURN 2; END IF;
  IF k = 'WEDNESDAY' THEN RETURN 3; END IF;
  IF k = 'THURSDAY' THEN RETURN 4; END IF;
  IF k = 'FRIDAY' THEN RETURN 5; END IF;
  IF k = 'SATURDAY' THEN RETURN 6; END IF;
  RETURN NULL;
END $$;

-- Next occurrence of a DOW on/after a date (0=Sun..6=Sat)
CREATE OR REPLACE FUNCTION public.next_weekday_on_or_after(from_date date, target_dow int)
RETURNS date
LANGUAGE sql IMMUTABLE AS $$
  SELECT from_date + ((target_dow - EXTRACT(DOW FROM from_date)::int + 7) % 7);
$$;

-- Ensure recurring slots for a single availability up to N weeks ahead
CREATE OR REPLACE FUNCTION public.ensure_recurring_slots_for_availability(avail_id bigint, weeks_ahead int DEFAULT 9)
RETURNS void
LANGUAGE plpgsql AS $$
DECLARE
  a_rec RECORD;
  target_dow int;
  start_date date;
  today date := current_date;
BEGIN
  SELECT availability_id, recurring, day_of_week
  INTO a_rec
  FROM public.tutor_availability
  WHERE availability_id = avail_id;

  IF NOT FOUND THEN
    RETURN;
  END IF;

  IF a_rec.recurring IS DISTINCT FROM TRUE THEN
    RETURN;
  END IF;

  target_dow := public.day_name_to_dow(a_rec.day_of_week);
  IF target_dow IS NULL THEN
    RAISE EXCEPTION 'Invalid day_of_week "%" for availability_id % (expected MONDAY..SUNDAY)', a_rec.day_of_week, avail_id;
  END IF;

  -- Start from the next target DOW on/after today
  start_date := public.next_weekday_on_or_after(today, target_dow);

  -- Insert one date per week up to weeks_ahead; rely on UNIQUE constraint to skip duplicates
  INSERT INTO public.slot_instance (availability_id, slot_date)
  SELECT avail_id, (start_date + (g.i * 7))::date
  FROM generate_series(0, GREATEST(weeks_ahead, 1) - 1) AS g(i)
  ON CONFLICT (availability_id, slot_date) DO NOTHING;
END $$;

-- Ensure recurring slots for all recurring availabilities up to N weeks
CREATE OR REPLACE FUNCTION public.ensure_recurring_slots_all(weeks_ahead int DEFAULT 9)
RETURNS void
LANGUAGE plpgsql AS $$
DECLARE
  rec RECORD;
BEGIN
  FOR rec IN
    SELECT availability_id
    FROM public.tutor_availability
    WHERE recurring IS TRUE
  LOOP
    PERFORM public.ensure_recurring_slots_for_availability(rec.availability_id, weeks_ahead);
  END LOOP;
END $$;


-- Seed slots after insert/update when recurring=true or when day_of_week changes
CREATE OR REPLACE FUNCTION public.tg_seed_recurring_slots()
RETURNS trigger
LANGUAGE plpgsql AS $$
BEGIN
  IF NEW.recurring IS TRUE THEN
    PERFORM public.ensure_recurring_slots_for_availability(NEW.availability_id, 9);
  END IF;
  RETURN NEW;
END $$;

DROP TRIGGER IF EXISTS trg_tutor_availability_after_insert ON public.tutor_availability;
CREATE TRIGGER trg_tutor_availability_after_insert
AFTER INSERT ON public.tutor_availability
FOR EACH ROW
EXECUTE FUNCTION public.tg_seed_recurring_slots();

DROP TRIGGER IF EXISTS trg_tutor_availability_after_update ON public.tutor_availability;
CREATE TRIGGER trg_tutor_availability_after_update
AFTER UPDATE OF recurring, day_of_week ON public.tutor_availability
FOR EACH ROW
WHEN (NEW.recurring IS TRUE)
EXECUTE FUNCTION public.tg_seed_recurring_slots();

-- Enable pg_cron (requires shared_preload_libraries = 'pg_cron' and DB restart)
CREATE EXTENSION IF NOT EXISTS pg_cron;

-- Optional: set cron to use your preferred timezone at the database level (adjust as needed)
-- Example sets cron timezone for THIS database only:
-- ALTER DATABASE current_database() SET cron.timezone = 'UTC';
-- Or set system-wide in postgresql.conf: cron.timezone = 'UTC'

-- Schedule a job to top-up every weekend.
-- Example 1: Run early Sunday morning at 00:10 (in cron.timezone)
SELECT cron.schedule(
  'ensure_recurring_slots_weekly_sun_0010',
  '10 0 * * 0',
  $$SELECT public.ensure_recurring_slots_all(9);$$
);

-- Alternative Example 2: Run early Saturday morning at 00:10
-- SELECT cron.schedule(
--   'ensure_recurring_slots_weekly_sat_0010',
--   '10 0 * * 6',
--   $$SELECT public.ensure_recurring_slots_all(9);$$
-- );

-- To run manually now (backfill):
-- SELECT public.ensure_recurring_slots_all(9);

-- To list jobs: SELECT * FROM cron.job;
-- To unschedule: SELECT cron.unschedule(jobid) FROM cron.job WHERE jobname = 'ensure_recurring_slots_weekly_sun_0010';