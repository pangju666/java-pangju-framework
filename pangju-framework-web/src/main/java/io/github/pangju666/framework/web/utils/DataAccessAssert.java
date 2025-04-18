/*
 *   Copyright 2025 pangju666
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package io.github.pangju666.framework.web.utils;

import io.github.pangju666.framework.web.exception.data.*;

public class DataAccessAssert {
	protected DataAccessAssert() {
	}

	public static void query(boolean expression) {
		if (!expression) {
			throw new DataQueryFailureException();
		}
	}

	public static void query(boolean expression, final String message) {
		if (!expression) {
			throw new DataQueryFailureException(message);
		}
	}

	public static void create(boolean expression) {
		if (!expression) {
			throw new DataCreateFailureException();
		}
	}

	public static void create(boolean expression, final String message) {
		if (!expression) {
			throw new DataCreateFailureException(message);
		}
	}

	public static void update(boolean expression) {
		if (!expression) {
			throw new DataUpdateFailureException();
		}
	}

	public static void update(boolean expression, final String message) {
		if (!expression) {
			throw new DataUpdateFailureException(message);
		}
	}

	public static void remove(boolean expression) {
		if (!expression) {
			throw new DataRemoveFailureException();
		}
	}

	public static void remove(boolean expression, final String message) {
		if (!expression) {
			throw new DataRemoveFailureException(message);
		}
	}

	public static void save(boolean expression) {
		if (!expression) {
			throw new DataSaveFailureException();
		}
	}

	public static void save(boolean expression, final String message) {
		if (!expression) {
			throw new DataSaveFailureException(message);
		}
	}
}
