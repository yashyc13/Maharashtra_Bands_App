# MVP final improvements checklist

## Error handling
- Show friendly messages for network/auth/storage failures in all screens.
- Reset one-time success/error flags after consumption.
- Log non-fatal errors in debug builds for faster triage.

## Loading states
- Disable action buttons while requests are in-flight.
- Show progress indicators for list refreshes and submits.
- Avoid double-submits by guarding multiple clicks.

## Offline support
- Keep Firestore offline persistence enabled in Application.
- Use cached reads for list screens when network is unavailable.
- Queue submissions locally and retry on network restore.

## Navigation hygiene
- Use a single activity with Navigation Component.
- Avoid manual fragment transactions unless required.
- Keep back stack predictable for admin flows.

## Stability
- Null-check view bindings and clear in onDestroyView.
- Avoid blocking UI thread with Firestore or Storage calls.
- Validate all user input before submission.

# Common bugs to avoid

- Forgetting composite indexes for combined Firestore filters + orderBy.
- Double-listener registration causing duplicate updates.
- Uploading large images without resizing or size limits.
- Missing auth checks on admin-only screens.
- Not handling ActivityNotFoundException for external intents.
- Using stale Fragment references after configuration changes.
