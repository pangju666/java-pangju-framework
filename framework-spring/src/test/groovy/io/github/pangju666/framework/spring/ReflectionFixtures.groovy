package io.github.pangju666.framework.spring

class ReflectionFixtures {
	static class User {
		private String name;
		private Integer age;
		public String nickname;
		private String hidden;

		private String secret() { return "secret"; }

		String getName() { return name; }

		void setName(String name) { this.name = name; }

		Integer getAge() { return age; }

		void setAge(Integer age) { this.age = age; }
	}

	static class NoAccessor {
		private String hidden;
	}

	static class GenericParent<T> {}

	static class GenericChild extends GenericParent<String> {}
}
