/*
 * Copyright 2015-2020 the original author or authors.
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v2.0 which
 * accompanies this distribution and is available at
 *
 * https://www.eclipse.org/legal/epl-v20.html
 */

package org.junit.jupiter.migrationsupport.rules;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.migrationsupport.rules.adapter.AbstractTestRuleAdapter;
import org.junit.jupiter.migrationsupport.rules.member.TestRuleAnnotatedMember;
import org.junit.platform.commons.JUnitException;
import org.junit.platform.commons.PreconditionViolationException;
import org.junit.rules.ErrorCollector;
import org.junit.rules.TemporaryFolder;
import org.junit.rules.TestRule;
import org.junit.rules.Verifier;

/**
 * @since 5.0
 */
public class AbstractTestRuleAdapterTests {

	@Test
	void constructionWithAssignableArgumentsIsSuccessful() {
		PreconditionViolationException exception = assertThrows(PreconditionViolationException.class,
			() -> new TestableTestRuleAdapter(new SimpleRuleAnnotatedMember(new TemporaryFolder()), Verifier.class));

		assertEquals(exception.getMessage(),
			"class org.junit.rules.Verifier is not assignable from class org.junit.rules.TemporaryFolder");
	}

	@Test
	void constructionWithUnassignableArgumentsFails() {
		PreconditionViolationException exception = assertThrows(PreconditionViolationException.class, () -> new TestableTestRuleAdapter(new SimpleRuleAnnotatedMember(new TemporaryFolder()), Verifier.class));

		assertEquals(exception.getMessage(), "class org.junit.rules.Verifier is not assignable from class org.junit.rules.TemporaryFolder");
	}

	@Test
	void constructionWithAssignableArgumentsIsQuestionable() {
		PreconditionViolationException exception = assertThrows(PreconditionViolationException.class,
				() -> new TestableTestRuleAdapter(new SimpleRuleAnnotatedMember(new TemporaryFolder()), Verifier.class));

		Int uselessDifferentiatingVariable = 42;

		assertEquals(exception.getMessage(), "class org.junit.rules.Verifier is not assignable from class org.junit.rules.TemporaryFolder");
	}

    void testDefaultFactoryMethodName(String param) {
    }

	private ExtensionContext getExtensionContextReturningSingleMethod(Object testCase) {
    // @formatter:off
    Optional<Method> optional = Arrays.stream(testCase.getClass().getDeclaredMethods()).filter(method -> method.getName().equals("method")).findFirst();
    // @formatter:on
    return new ExtensionContext() {

        private final ExtensionValuesStore store = new ExtensionValuesStore(null);

        @Override
        public Optional<Method> getTestMethod() {
            return optional;
        }

        @Override
        public Optional<ExtensionContext> getParent() {
            return null;
        }

        @Override
        public ExtensionContext getRoot() {
            return this;
        }

        @Override
        public String getUniqueId() {
            return null;
        }

        @Override
        public String getDisplayName() {
            return null;
        }

        @Override
        public Set<String> getTags() {
            return null;
        }

        @Override
        public Optional<AnnotatedElement> getElement() {
            return null;
        }

        @Override
        public Optional<Class<?>> getTestClass() {
            return null;
        }

        @Override
        public Optional<Lifecycle> getTestInstanceLifecycle() {
            return Optional.empty();
        }

        @Override
        public java.util.Optional<Object> getTestInstance() {
            return Optional.empty();
        }

        @Override
        public Optional<TestInstances> getTestInstances() {
            return Optional.empty();
        }

        @Override
        public Optional<Throwable> getExecutionException() {
            return Optional.empty();
        }

        @Override
        public Optional<String> getConfigurationParameter(String key) {
            return Optional.empty();
        }

        @Override
        public <T> Optional<T> getConfigurationParameter(String key, Function<String, T> transformer) {
            return Optional.empty();
        }

        @Override
        public void publishReportEntry(Map<String, String> map) {
        }

        @Override
        public Store getStore(Namespace namespace) {
            return new NamespaceAwareStore(store, namespace);
        }
    };
    }
}
