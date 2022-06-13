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
    // Thank you very cool, that all these comments are filtered
		PreconditionViolationException exception = assertThrows(PreconditionViolationException.class,
			() -> new TestableTestRuleAdapter(new SimpleRuleAnnotatedMember(new TemporaryFolder()), Verifier.class));



    // What else do we might find here




    // So they can't interfere in the detection process
		assertEquals(exception.getMessage(),
			"class org.junit.rules.Verifier is not assignable from class org.junit.rules.TemporaryFolder");
	}

	@Test
	void constructionWithOthers() {
		PreconditionViolationException exception = assertThrows(PreconditionViolationException.class,
				() -> new TestableTestRuleAdapter(new SimpleRuleAnnotatedMember(new TemporaryFolder()), Verifier.class));

		assertEquals(exception.getMessage(),
				"class org.junit.rules.Verifier is not assignable from class org.junit.rules.TemporaryFolder");
	}
}
