/*
 * Copyright 2012-2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.samples.petclinic.owner;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * VIS-142: allowed and blocked visit status changes.
 */
class VisitTests {

	@Test
	void newVisitStartsScheduled() {
		assertEquals(VisitStatus.SCHEDULED, new Visit().getStatus());
	}

	@Test
	void scheduledCanBeCompleted() {
		Visit visit = new Visit();

		visit.complete();

		assertEquals(VisitStatus.COMPLETED, visit.getStatus());
	}

	@Test
	void scheduledCanBeCancelled() {
		Visit visit = new Visit();

		visit.cancel();

		assertEquals(VisitStatus.CANCELLED, visit.getStatus());
	}

	@Test
	void scheduledCannotStayScheduled() {
		Visit visit = new Visit();

		assertThrows(IllegalStateException.class, () -> visit.changeStatus(VisitStatus.SCHEDULED));
		assertEquals(VisitStatus.SCHEDULED, visit.getStatus());
	}

	@Test
	void completedCannotChange() {
		Visit visit = new Visit();
		visit.complete();

		assertThrows(IllegalStateException.class, visit::complete);
		assertThrows(IllegalStateException.class, visit::cancel);
		assertThrows(IllegalStateException.class, () -> visit.changeStatus(VisitStatus.SCHEDULED));
		assertEquals(VisitStatus.COMPLETED, visit.getStatus());
	}

	@Test
	void cancelledCannotChange() {
		Visit visit = new Visit();
		visit.cancel();

		assertThrows(IllegalStateException.class, visit::complete);
		assertThrows(IllegalStateException.class, visit::cancel);
		assertThrows(IllegalStateException.class, () -> visit.changeStatus(VisitStatus.SCHEDULED));
		assertEquals(VisitStatus.CANCELLED, visit.getStatus());
	}

	@Test
	void nullTargetIsBlocked() {
		Visit visit = new Visit();

		assertThrows(IllegalStateException.class, () -> visit.changeStatus(null));
		assertEquals(VisitStatus.SCHEDULED, visit.getStatus());
	}

}
