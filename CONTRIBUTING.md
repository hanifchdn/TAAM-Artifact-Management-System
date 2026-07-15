# Branching Workflow

To keep the repository organized, contributors should **not directly commit to the `main` branch**.

Every new feature, improvement, or bug fix should be developed on a separate branch.

## Branch Naming Convention

Use the following naming format:

```
type/name/description
```

Where:

- `type` describes the purpose of the branch
- `name` is the contributor's name
- `description` briefly describes the change

## Examples

Feature branches:

```
feature/hanif/login-system
feature/hanif/database-setup
feature/hanif/user-interface
```

Bug fix branches:

```
bugfix/hanif/fix-login-error
bugfix/hanif/fix-null-pointer
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
