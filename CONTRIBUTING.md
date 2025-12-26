# Contributing to MSGuis

Thank you for your interest in contributing to MSGuis! This document provides guidelines and instructions for contributing to the project.

## Table of Contents

- [Code of Conduct](#code-of-conduct)
- [Getting Started](#getting-started)
- [Development Setup](#development-setup)
- [Project Structure](#project-structure)
- [Coding Standards](#coding-standards)
- [Testing](#testing)
- [Submitting Changes](#submitting-changes)
- [Documentation](#documentation)

## Code of Conduct

By participating in this project, you agree to:

- Be respectful and inclusive
- Welcome newcomers and help them get started
- Accept constructive criticism gracefully
- Focus on what is best for the community
- Show empathy towards other community members

## Getting Started

### Prerequisites

- Java 17 or higher
- Gradle 7.0+
- Git
- A Java IDE (IntelliJ IDEA recommended)

### Forking the Repository

1. Fork the repository on GitHub
2. Clone your fork locally:
   ```bash
   git clone https://github.com/YOUR_USERNAME/msguis.git
   cd msguis
   ```
3. Add the upstream repository:
   ```bash
   git remote add upstream https://github.com/echolightmc/msguis.git
   ```

## Development Setup

### Building the Project

```bash
./gradlew build
```

### Running Tests

```bash
./gradlew test
```

### IDE Setup

#### IntelliJ IDEA

1. Open IntelliJ IDEA
2. File → Open → Select the `msguis` directory
3. Trust the project and wait for Gradle to sync
4. Install the Minestom plugin (if available)

#### Eclipse

1. Import as Gradle project
2. Wait for workspace to build

## Project Structure

```
msguis/
├── src/
│   ├── main/
│   │   └── java/
│   │       └── com/github/echolightmc/msguis/
│   │           ├── ChestGUI.java          # Base GUI class
│   │           ├── NormalGUI.java         # Standard GUI
│   │           ├── ScrollGUI.java         # Scrollable GUI
│   │           ├── GUIManager.java        # Event manager
│   │           ├── GUIItem.java           # Base item class
│   │           ├── StaticGUIItem.java     # Static items
│   │           ├── DynamicGUIItem.java    # Dynamic items
│   │           ├── ScrollGUIItem.java     # Scroll buttons
│   │           ├── ChestType.java         # Chest sizes enum
│   │           └── Indicator.java         # Slot indicators enum
│   └── test/
│       └── java/
│           ├── DemoMain.java              # Test server
│           └── ScrollCommand.java         # Example command
├── gradle/                                 # Gradle wrapper
├── build.gradle                           # Build configuration
├── settings.gradle                        # Project settings
├── README.md                              # Main documentation
├── API.md                                 # API reference
├── CONTRIBUTING.md                        # This file
└── LICENSE                                # License file
```

## Coding Standards

### Java Style

Follow standard Java conventions:

- **Indentation**: Tabs (not spaces)
- **Naming**:
  - Classes: `PascalCase`
  - Methods: `camelCase`
  - Constants: `UPPER_SNAKE_CASE`
  - Variables: `camelCase`
- **Braces**: Egyptian style (opening brace on same line)
- **Line Length**: Aim for 120 characters max

### Example

```java
public class MyGUIComponent extends ChestGUI {
	
	private static final int MAX_ITEMS = 54;
	private final List<GUIItem> items;
	
	public MyGUIComponent(GUIManager manager) {
		super(manager, createDefaultFormat(), new HashMap<>(), new HashMap<>());
		this.items = new ArrayList<>();
	}
	
	public void addItem(GUIItem item) {
		if (items.size() >= MAX_ITEMS) {
			throw new IllegalStateException("Cannot add more items");
		}
		items.add(item);
	}
	
}
```

### JavaDoc Requirements

All public classes, methods, and fields must have JavaDoc comments:

```java
/**
 * Brief description of the class.
 * <p>
 * More detailed description if needed.
 * Multiple paragraphs are fine.
 * <p>
 * <b>Example:</b>
 * <pre>{@code
 * // Usage example
 * MyClass obj = new MyClass();
 * }</pre>
 *
 * @see RelatedClass
 * @since 1.0.0
 */
public class MyClass {
	
	/**
	 * Brief description of the method.
	 * <p>
	 * Detailed explanation of what this method does,
	 * any important behavior, and edge cases.
	 *
	 * @param parameter description of parameter
	 * @return description of return value
	 * @throws ExceptionType when this exception is thrown
	 */
	public ReturnType myMethod(ParameterType parameter) {
		// Implementation
	}
	
}
```

### Import Organization

- Group imports: Java standard library, then third-party, then internal
- No wildcard imports (except for static imports when appropriate)
- Remove unused imports

```java
import java.util.ArrayList;
import java.util.List;

import net.minestom.server.entity.Player;
import net.minestom.server.inventory.Inventory;

import com.github.echolightmc.msguis.ChestGUI;
```

## Testing

### Writing Tests

- Place test files in `src/test/java/`
- Test file names should end with `Test.java`
- Use JUnit 5 for testing
- Aim for high code coverage

Example test:

```java
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class GUIManagerTest {
	
	@Test
	void testGUIRegistration() {
		GUIManager manager = new GUIManager(eventHandler);
		NormalGUI gui = NormalGUI.builder()
			.manager(manager)
			.format("#########")
			.item('#', new StaticGUIItem(ItemStack.AIR))
			.build();
		
		assertNotNull(gui);
		assertEquals(manager, gui.getGuiManager());
	}
	
}
```

### Running Tests

```bash
# Run all tests
./gradlew test

# Run specific test class
./gradlew test --tests GUIManagerTest

# Run with coverage
./gradlew test jacocoTestReport
```

## Submitting Changes

### Branch Naming

Use descriptive branch names:

- `feature/add-xyz` - New features
- `fix/issue-123` - Bug fixes
- `docs/improve-xyz` - Documentation updates
- `refactor/cleanup-xyz` - Code refactoring
- `test/add-xyz-tests` - Adding tests

### Commit Messages

Write clear, descriptive commit messages:

```
[Type] Brief description (50 chars or less)

More detailed explanation if needed. Wrap at 72 characters.
Explain what and why, not how.

- Bullet points are okay
- Use imperative mood ("Add feature" not "Added feature")

Fixes #123
```

**Types:**
- `feat` - New feature
- `fix` - Bug fix
- `docs` - Documentation only
- `style` - Formatting, missing semicolons, etc.
- `refactor` - Code change that neither fixes a bug nor adds a feature
- `test` - Adding or updating tests
- `chore` - Maintenance tasks

### Pull Request Process

1. **Update your fork:**
   ```bash
   git fetch upstream
   git rebase upstream/master
   ```

2. **Create a branch:**
   ```bash
   git checkout -b feature/my-new-feature
   ```

3. **Make your changes:**
   - Write code following the style guide
   - Add tests for new functionality
   - Update documentation

4. **Commit your changes:**
   ```bash
   git add .
   git commit -m "[feat] Add new feature"
   ```

5. **Push to your fork:**
   ```bash
   git push origin feature/my-new-feature
   ```

6. **Create a Pull Request:**
   - Go to the GitHub repository
   - Click "New Pull Request"
   - Select your branch
   - Fill out the PR template
   - Link any related issues

### Pull Request Checklist

Before submitting, ensure:

- [ ] Code follows the style guide
- [ ] All tests pass
- [ ] New code has tests
- [ ] Public APIs have JavaDoc
- [ ] README updated if needed
- [ ] API.md updated if needed
- [ ] No merge conflicts
- [ ] Commit messages are clear
- [ ] PR description explains changes

## Documentation

### Updating Documentation

When making changes that affect the public API:

1. **Update JavaDoc** in the source files
2. **Update README.md** with new examples or usage instructions
3. **Update API.md** with new methods or classes
4. **Add examples** showing how to use new features

### Documentation Style

- Use clear, concise language
- Provide code examples
- Explain both what and why
- Include edge cases and warnings
- Use proper formatting (code blocks, lists, tables)

### Example Documentation

```markdown
### New Feature: Custom Click Handlers

You can now customize click behavior for static items:

#### Usage

\`\`\`java
StaticGUIItem customItem = new StaticGUIItem(itemStack) {
    @Override
    public void handleClick(InventoryPreClickEvent event) {
        // Custom behavior
        event.getPlayer().sendMessage("Custom click!");
        event.setCancelled(true);
    }
};
\`\`\`

#### Parameters

- `itemStack` - The item to display

#### Notes

- The click event must be manually cancelled
- This overrides the default static behavior
```

## Feature Requests and Bug Reports

### Reporting Bugs

When reporting bugs, include:

- **Description**: Clear description of the bug
- **Steps to Reproduce**: Numbered steps to reproduce
- **Expected Behavior**: What you expected to happen
- **Actual Behavior**: What actually happened
- **Environment**:
  - OS and version
  - Java version
  - Minestom version
  - MSGuis version
- **Code Sample**: Minimal code that reproduces the issue
- **Stack Trace**: Full error message and stack trace

### Requesting Features

When requesting features, include:

- **Description**: Clear description of the feature
- **Use Case**: Why this feature is needed
- **Example**: How you envision using it
- **Alternatives**: Other ways you've tried to solve this
- **Implementation Ideas**: Any thoughts on how it could be implemented

## Questions?

If you have questions about contributing:

- Open a GitHub issue with the `question` label
- Check existing issues and pull requests
- Review the documentation

## License

By contributing to MSGuis, you agree that your contributions will be licensed under the same license as the project.

---

Thank you for contributing to MSGuis! 🎉
