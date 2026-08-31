package com.yasirceltik.promptvault.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

import com.yasirceltik.promptvault.dto.CreatePolicyKeywordDto;
import com.yasirceltik.promptvault.model.PolicyKeyword;
import com.yasirceltik.promptvault.model.User;
import com.yasirceltik.promptvault.repository.PolicyKeywordRepository;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;

class LogInjectionTest {

    @Test
    void serviceLogDoesNotIncludeUntrustedKeywordText() {
        PolicyKeywordRepository repository = mock(PolicyKeywordRepository.class);
        when(repository.existsByContentIgnoreCase(any())).thenReturn(false);
        when(repository.save(any(PolicyKeyword.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PolicyKeywordService service = new PolicyKeywordService(repository);
        User admin = User.builder().id(42L).build();
        String malicious = "keyword\r\n2026-08-31 ERROR forged\t" + "x".repeat(1_000);

        Logger logger = (Logger) LoggerFactory.getLogger(PolicyKeywordService.class);
        ListAppender<ILoggingEvent> appender = new ListAppender<>();
        appender.start();
        logger.addAppender(appender);

        try {
            service.createKeyword(new CreatePolicyKeywordDto(malicious), admin);
        } finally {
            logger.detachAppender(appender);
            appender.stop();
        }

        assertEquals(1, appender.list.size());
        String event = appender.list.getFirst().getFormattedMessage();
        assertEquals("created policy keyword id=0", event);
        assertFalse(event.contains("\r"));
        assertFalse(event.contains("\n"));
        assertFalse(event.contains("\t"));
        assertFalse(event.contains("forged"));
    }
}
