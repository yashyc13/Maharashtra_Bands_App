# Admin access control

## Recommended approach

- **Firebase Authentication** with email/password for admin users only.
- **Custom claims**: mark admin users with a claim like `admin: true`.
- **Firestore & Storage rules**: restrict administrative collections and write operations to users with the admin claim.

This keeps UI checks lightweight while enforcing security on the backend.
