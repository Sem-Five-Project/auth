-- Create optimized find_tutors function for efficient tutor search
-- Returns JSON directly for maximum performance and reduced server load

CREATE OR REPLACE FUNCTION find_tutors(
  p_min_rating numeric DEFAULT NULL,
  p_min_experience integer DEFAULT NULL,
  p_min_completion_rate numeric DEFAULT NULL,
  p_subject_id integer DEFAULT NULL,
  p_min_price numeric DEFAULT NULL,
  p_max_price numeric DEFAULT NULL,
  p_search_keyword text DEFAULT '',
  p_sort_by text DEFAULT 'rating',
  p_sort_order_asc boolean DEFAULT false,
  p_page integer DEFAULT 1,
  p_page_size integer DEFAULT 20
)
RETURNS json AS $$
DECLARE
  offset_val integer;
BEGIN
  -- Calculate offset for pagination
  offset_val := (p_page - 1) * p_page_size;
  
  RETURN (
    WITH filtered_tutors AS (
      SELECT DISTINCT 
        tp.tutor_id,
        tp.rating,
        tp.experience_in_months,
        tp.class_completion_rate,
        tp.bio,
        tp.hourly_rate as base_hourly_rate,
        u.first_name,
        u.last_name,
        u.email,
        u.profile_image,
        ts.subject_id,
        ts.hourly_rate as subject_hourly_rate,
        s.name AS subject_name,
        l.name AS language_name,
        -- Calculate relevance score
        CASE 
          -- Exact name match gets highest score
          WHEN LOWER(CONCAT(u.first_name, ' ', u.last_name)) = LOWER(p_search_keyword) THEN 100.0
          -- Partial name match
          WHEN LOWER(CONCAT(u.first_name, ' ', u.last_name)) LIKE LOWER('%' || p_search_keyword || '%') THEN 90.0
          -- Subject exact match
          WHEN LOWER(s.name) = LOWER(p_search_keyword) THEN 95.0
          -- Subject partial match
          WHEN LOWER(s.name) LIKE LOWER('%' || p_search_keyword || '%') THEN 80.0
          -- Bio match
          WHEN tp.bio IS NOT NULL AND LOWER(tp.bio) LIKE LOWER('%' || p_search_keyword || '%') THEN 70.0
          -- Special partial matches for common abbreviations
          WHEN (p_search_keyword = 'his' AND LOWER(s.name) LIKE '%history%') THEN 88.0
          WHEN (p_search_keyword = 'math' AND LOWER(s.name) LIKE '%mathematics%') THEN 88.0
          WHEN (p_search_keyword = 'phys' AND LOWER(s.name) LIKE '%physics%') THEN 88.0
          WHEN (p_search_keyword = 'chem' AND LOWER(s.name) LIKE '%chemistry%') THEN 88.0
          ELSE 50.0
        END +
        -- Quality bonuses
        CASE WHEN tp.rating >= 4.5 THEN 5.0 ELSE 0.0 END +
        CASE WHEN tp.class_completion_rate >= 90.0 THEN 3.0 ELSE 0.0 END
        as relevance_score
      FROM tutor_profiles tp
      INNER JOIN users u ON tp.user_id = u.id
      INNER JOIN tutor_subjects ts ON tp.tutor_id = ts.tutor_id 
        AND (ts.verification = 'APPROVED' OR ts.verification IS NULL)
      INNER JOIN subjects s ON ts.subject_id = s.subject_id
      LEFT JOIN tutor_languages tl ON tp.tutor_id = tl.tutor_id
      LEFT JOIN languages l ON tl.language_id = l.language_id
      WHERE 
        tp.verified = true
        AND (p_min_rating IS NULL OR tp.rating >= p_min_rating)
        AND (p_min_experience IS NULL OR tp.experience_in_months >= p_min_experience)
        AND (p_min_completion_rate IS NULL OR tp.class_completion_rate >= p_min_completion_rate)
        AND (p_subject_id IS NULL OR ts.subject_id = p_subject_id)
        AND (p_min_price IS NULL OR ts.hourly_rate >= p_min_price)
        AND (p_max_price IS NULL OR ts.hourly_rate <= p_max_price)
        AND (
          p_search_keyword = '' OR 
          LOWER(u.first_name) LIKE LOWER('%' || p_search_keyword || '%') OR
          LOWER(u.last_name) LIKE LOWER('%' || p_search_keyword || '%') OR
          LOWER(s.name) LIKE LOWER('%' || p_search_keyword || '%') OR
          (tp.bio IS NOT NULL AND LOWER(tp.bio) LIKE LOWER('%' || p_search_keyword || '%')) OR
          -- Special partial matches
          (p_search_keyword = 'his' AND LOWER(s.name) LIKE '%history%') OR
          (p_search_keyword = 'math' AND LOWER(s.name) LIKE '%mathematics%') OR
          (p_search_keyword = 'phys' AND LOWER(s.name) LIKE '%physics%') OR
          (p_search_keyword = 'chem' AND LOWER(s.name) LIKE '%chemistry%')
        )
    ),
    aggregated_tutors AS (
      SELECT 
        tutor_id,
        first_name,
        last_name,
        email,
        profile_image,
        rating,
        experience_in_months,
        class_completion_rate,
        bio,
        base_hourly_rate,
        AVG(relevance_score) as avg_relevance_score,
        STRING_AGG(DISTINCT subject_name, ', ' ORDER BY subject_name) AS subject_names,
        STRING_AGG(DISTINCT CONCAT(subject_hourly_rate::text, '/hr'), ', ' ORDER BY subject_hourly_rate) AS hourly_rates,
        STRING_AGG(DISTINCT language_name, ', ' ORDER BY language_name) AS languages,
        MIN(subject_hourly_rate) as min_hourly_rate,
        MAX(subject_hourly_rate) as max_hourly_rate
      FROM filtered_tutors
      GROUP BY tutor_id, first_name, last_name, email, profile_image, rating, experience_in_months, class_completion_rate, bio, base_hourly_rate
    ),
    sorted_tutors AS (
      SELECT *
      FROM aggregated_tutors
      ORDER BY 
        CASE 
          WHEN p_sort_by = 'rating' AND p_sort_order_asc THEN rating
        END ASC,
        CASE 
          WHEN p_sort_by = 'rating' AND NOT p_sort_order_asc THEN rating
        END DESC,
        CASE 
          WHEN p_sort_by = 'experience' AND p_sort_order_asc THEN experience_in_months 
        END ASC,
        CASE 
          WHEN p_sort_by = 'experience' AND NOT p_sort_order_asc THEN experience_in_months 
        END DESC,
        CASE 
          WHEN p_sort_by = 'price' AND p_sort_order_asc THEN min_hourly_rate  
        END ASC,
        CASE 
          WHEN p_sort_by = 'price' AND NOT p_sort_order_asc THEN min_hourly_rate  
        END DESC,
        CASE 
          WHEN p_sort_by = 'completion_rate' AND p_sort_order_asc THEN class_completion_rate 
        END ASC,
        CASE 
          WHEN p_sort_by = 'completion_rate' AND NOT p_sort_order_asc THEN class_completion_rate 
        END DESC,
        CASE 
          WHEN p_sort_by = 'relevance' AND p_sort_order_asc THEN avg_relevance_score 
        END ASC,
        CASE 
          WHEN p_sort_by = 'relevance' AND NOT p_sort_order_asc THEN avg_relevance_score 
        END DESC,
        -- Default sort when no specific sort matches
        rating DESC, experience_in_months DESC
      LIMIT p_page_size 
      OFFSET offset_val
    ),
    total_count AS (
      SELECT COUNT(DISTINCT tutor_id) AS total_results FROM filtered_tutors
    )
    SELECT json_build_object(
      'results', COALESCE(json_agg(
        json_build_object(
          'resultType', 'tutor',
          'id', st.tutor_id,
          'title', CONCAT(st.first_name, ' ', st.last_name),
          'description', st.bio,
          'rating', st.rating,
          'experienceMonths', st.experience_in_months,
          'subjectName', st.subject_names,
          'tutorName', CONCAT(st.first_name, ' ', st.last_name),
          'tutorFirstName', st.first_name,
          'tutorLastName', st.last_name,
          'maxDays', NULL,
          'schedule', NULL,
          'hourlyRate', st.base_hourly_rate,
          'hourlyRates', st.hourly_rates,
          'classType', 'tutor',
          'relevanceScore', st.avg_relevance_score,
          'bio', st.bio,
          'classCompletionRate', st.class_completion_rate,
          'languages', st.languages,
          'email', st.email,
          'profileImage', st.profile_image
        )
      ), '[]'::json),
      'metadata', (
        SELECT json_build_object(
          'totalResults', COALESCE(total_results, 0),
          'currentPage', p_page,
          'totalPages', CASE WHEN p_page_size > 0 THEN CEIL(COALESCE(total_results, 0)::numeric / p_page_size) ELSE 0 END,
          'searchQuery', p_search_keyword,
          'pageSize', p_page_size
        )
        FROM total_count
      )
    )
    FROM sorted_tutors st
  );
END;
$$ LANGUAGE plpgsql STABLE;

-- Create indexes for optimal performance
CREATE INDEX IF NOT EXISTS idx_tutor_profiles_rating ON tutor_profiles(rating DESC);
CREATE INDEX IF NOT EXISTS idx_tutor_profiles_experience ON tutor_profiles(experience_in_months DESC);
CREATE INDEX IF NOT EXISTS idx_tutor_profiles_completion_rate ON tutor_profiles(class_completion_rate DESC);
CREATE INDEX IF NOT EXISTS idx_tutor_profiles_verified ON tutor_profiles(verified);

CREATE INDEX IF NOT EXISTS idx_tutor_subjects_hourly_rate ON tutor_subjects(hourly_rate);
CREATE INDEX IF NOT EXISTS idx_tutor_subjects_verification ON tutor_subjects(verification);

CREATE INDEX IF NOT EXISTS idx_users_names ON users(first_name, last_name);
CREATE INDEX IF NOT EXISTS idx_subjects_name_lower ON subjects(LOWER(name));

-- Composite indexes for common filter combinations
CREATE INDEX IF NOT EXISTS idx_tutors_search_composite ON tutor_profiles(verified, rating DESC, experience_in_months DESC) 
  WHERE verified = true;

CREATE INDEX IF NOT EXISTS idx_tutor_subjects_composite ON tutor_subjects(tutor_id, subject_id, hourly_rate);