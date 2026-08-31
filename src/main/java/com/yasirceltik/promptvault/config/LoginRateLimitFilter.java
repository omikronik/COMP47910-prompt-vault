package com.yasirceltik.promptvault.config;

import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import io.github.bucket4j.Refill;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class LoginRateLimitFilter extends OncePerRequestFilter {

    private static final int REQUEST_LIMIT = 10;

    private static final Duration REFILL_PERIOD =
            Duration.ofMinutes(1);

    private final Cache<String, Bucket> buckets =
            Caffeine.newBuilder()
                    .expireAfterAccess(
                            30,
                            TimeUnit.MINUTES
                    )
                    .maximumSize(10_000)
                    .build();

    @Override
    protected boolean shouldNotFilter(
            HttpServletRequest request) {

        return !(
                request.getMethod().equalsIgnoreCase("POST")
                && request.getRequestURI()
                        .equals("/auth/login")
        );
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String clientIp =
                getClientIp(request);

        Bucket bucket =
                buckets.get(
                        clientIp,
                        key -> createBucket()
                );

        ConsumptionProbe probe =
                bucket.tryConsumeAndReturnRemaining(1);

        if (probe.isConsumed()) {
            filterChain.doFilter(
                    request,
                    response
            );

            return;
        }

        long retryAfterSeconds =
                Math.max(
                        1,
                        TimeUnit.NANOSECONDS.toSeconds(
                                probe.getNanosToWaitForRefill()
                        )
                );

        response.setStatus(
                HttpStatus.TOO_MANY_REQUESTS.value()
        );

        response.setHeader(
                HttpHeaders.RETRY_AFTER,
                Long.toString(retryAfterSeconds)
        );

        response.setContentType(
                "text/plain;charset=UTF-8"
        );

        response.getWriter().write(
                "Too many login attempts. Please try again later."
        );
    }

    private Bucket createBucket() {
        Refill refill =
                Refill.intervally(
                        REQUEST_LIMIT,
                        REFILL_PERIOD
                );

        Bandwidth limit =
                Bandwidth.classic(
                        REQUEST_LIMIT,
                        refill
                );

        return Bucket.builder()
                .addLimit(limit)
                .build();
    }

    private String getClientIp(
            HttpServletRequest request) {

        return request.getRemoteAddr();
    }
}
