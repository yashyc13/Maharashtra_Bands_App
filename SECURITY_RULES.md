# Firebase security rules overview

## Firestore rules

- `bands`: anyone can read **approved** bands only, since listings are public. Admins can create, update, and delete bands for moderation.
- `submissions`: anyone can create new submissions. Only admins can read, update, or delete submissions for review/approval.
- `isAdmin()` uses a custom claim (`admin: true`) on the authenticated user token to secure admin-only writes.

## Storage rules

- `submissions/`: public uploads are allowed for optional user images. This mirrors the requirement for public submissions.
- `bands/`: reads are public for displaying band images, but writes are limited to admins only.

> **Note:** for stricter controls, you can validate file size and MIME type in Storage rules or require authenticated users to upload submissions.
