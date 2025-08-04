package com.edu.tutor_platform.vidConf.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

// import com.example.jitsiserver.config.JwtUtil;
import com.edu.tutor_platform.config.JwtUtil;
import org.springframework.stereotype.Service;

import java.security.interfaces.RSAPrivateKey;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class TokenService {

    private final String appId = "vpaas-magic-cookie-9a14da2c52de4d2da4c1ab82635750b3"; // Replace with JaaS App ID
    private final String tenant ="vpaas-magic-cookie-9a14da2c52de4d2da4c1ab82635750b3"; // Replace with JaaS tenant
    private final String keyId = "vpaas-magic-cookie-9a14da2c52de4d2da4c1ab82635750b3/2bb5c1" + //
                ""; 
    private final String privateKey = """
            -----BEGIN PRIVATE KEY-----
MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCmmb/dAimwfq8+
//YRPXxUcbOvUEoeTP0RpX0T78JjqtQKiCkgQ7/jwmnR7iuxItvieKLacGLUXiMW
hVE8XIhiSfokbS6Or3L21IF3iRCcwy4oeeGY5XrVnczV1rWpa7fkdwpqhnvQjnSd
XBACTqszNWrVp7G8pq46Ai4JMYyOpJAXdBw2bgNnHNdi4MPrP19Loc8aa8Pw+8Ft
Zt7kh48sN6/bfTUi+glbG4qCnqUgDS0S17vZRB1xXmr2//GqJpA08CJyu6VBpOzt
F3qBCtyUBDpX7xXtq5wM0UC4U3/LBDI9tZKWpTlE+UMo3F66tkHEbTA/H7Q2Z7MP
Q0230/GfAgMBAAECggEAXYDBLBtApqwTUzp5Mmfd1c8S1/wp/SOcjUDiGERiR2nj
JB4TZuZfVkcQ+R/BKeO0oUCvtgWJkkNbUhJ063nLmc6HM9aLYM2bqmjOWPZ2emUZ
DOKK+TMiY63y/rlIr9OjG/irwenEp+KsFH1NBt58O3tpgIxjtsud0TTT/ZjvmEZf
0uLfoF5oStujlNEzCHlI/efFNyK5ZjhaRsUwvNEaclxIp+8IXIEpulKvDtxFXrpN
kDAxK/1QBBKrpaWJxbo8/wf2SIAc82by04QZJSZTMLVCqDF209uCxpvsxNjuaNZC
/2jpg/nxY9DxeS/pS6LoGWmZYUAfoQbHfopgu2mJ8QKBgQDbaS12d519xEJ4jeaJ
NBcVOdT99BWIgsUaaMf4KRuHOi18eNObq8PjfIducofTTfZdPAPv2PvX8mEADRKv
DMcKvQ0Xo1NFMdSF2jvHlZG4mfFt3+7khZr3WqYMoTD88mCqLm+3PqHtQKHHI70z
nnJxRQEnnIOW9TjrZPHCEkKmEwKBgQDCYg9XHg3OeEztgdtJXIXj4CgU58DskWeT
xeM8vipSDvlg40xK605vu1Sjr+hagbRI/oAoLVZma7hV2dAzGWYg1TZ7izm7z5as
6AU3m+zIx9yEBQy92T62sYmZK8QgMqdf4BXQHkNf2rukii+XKNG3Gw/daHvlyjiI
vQcX4HXnxQKBgEAa/tl6IV25leI7M3wq6AOfngeE5TW5X3VT3/k1qYFdEiUn+Eun
Z9hzbe5rMBwgzzZ2boAJWavCeWzWZPsYNJ+ClOyvH4k8AegJ1yV9IdJZyBxeLSI/
lwNmTNWr0vBUsMGIR8O2XQFAvQO+AW2vgahujOPR6vCUPUkpeSlGxIpZAoGBALzg
C4fa7A9zVUxyoWDnRjHl3QxzJEK6BqrP3b1oCjcx1Z8PPVKO0eg/Xj6u7lcDMOq8
YEqpwwpCIRtKScNdKp88jXP+FFspmn7n9CnUmH9/tAWT4o2WShm66LisjiW7EmdW
ldtjjJPXNfzrSNLYFu6UnRXGMJ1XoRc8lWfd8rMBAoGAd4hjKUz98sL4t+89UFWi
LehF92gik3/l8SBq/c3i84Is8z221dli/YbwfRx9CxcYv0DG9DCbCyClsuMJI4YJ
13Xzk52p8yyz7WTKdg9Z/s8WBoauw4R3kL82LoAcZaHydfccW/+lZFg0E2b5dKig
7BN/C4AjVIbGspynhxgtrOc=
-----END PRIVATE KEY-----
            """;

    public String generateToken(String roomName, String userName) {
        try {
            RSAPrivateKey key = JwtUtil.getPrivateKeyFromString(privateKey);

            Algorithm algorithm = Algorithm.RSA256(null, key);

            Map<String, Object> headerClaims = new HashMap<>();
            headerClaims.put("kid", keyId);

            Map<String, Object> context = new HashMap<>();
            Map<String, Object> user = new HashMap<>();
            user.put("name", userName);
            context.put("user", user);

            Map<String, Object> features = new HashMap<>();
        features.put("livestreaming", true);
        features.put("recording", true);
        features.put("outbound-call", true);
        features.put("transcription", true);

            String token = JWT.create()
                    .withHeader(headerClaims)
                    .withIssuer("chat")
                    .withAudience("jitsi")
                    .withSubject(tenant)
                    .withClaim("room", roomName)
                    .withClaim("context", context)
                    .withClaim("moderator", true)
                    // .withClaim("iss", "chat")
                    .withClaim("features", features)
                    .withExpiresAt(Date.from(Instant.now().plusSeconds(3600)))
                    .sign(algorithm);

            return token;

        } catch (Exception e) {
            throw new RuntimeException("Token generation failed", e);
        }
    }
}
