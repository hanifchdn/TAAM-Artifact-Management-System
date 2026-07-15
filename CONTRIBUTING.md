# Branching Workflow

To keep the repository organized, contributors should **not directly commit to the `main` branch**.

Every new feature, improvement, or bug fix should be developed on a separate branch.

## Branch Naming Convention

Use the following naming format:

```
type/B07-x/description
```

Where:

- `type` describes the purpose of the branch
- `B07-x` is the reference to jira task
- `description` briefly describes the change

## Examples

Feature branches:

```
feature/B07-1/login-system
feature/B07-2/database-setup
feature/B07-3/user-interface
```

Bug fix branches:

```
bugfix/B07-4/fix-login-error
bugfix/B07-5/fix-null-pointer
```

---

# Commit Guidelines

This project follows the **Conventional Commits** format. All commits should begin with a commit type followed by a short description of the change.

The format is:

```
<type>: <description>
```

Example:

```
docs: add README installation instructions
fix: fix bugs in buf.file handling
feat: add user authentication system
```
