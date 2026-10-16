# Knowledge Library Android APK

A separate, universal offline Android app for importing and browsing hierarchical knowledge outlines as plain-text `.txt` files.

## Features
- Import any `.txt` outline through Android's document picker.
- First line becomes the package title.
- Four spaces per hierarchy level; local numbering like `1.` is stripped from labels.
- Search across package titles and all concept titles.
- Browse categories, open a concept, expand/collapse branches, and navigate parent paths.
- Save multiple packages locally in WebView local storage.
- Replace an existing package with the same title, or delete a package.
- No network permission and no internet connection required after installation.

## Outline format
Save your generated outline as UTF-8 plain text. Example:

    Biology
    1. Foundations
        1. Cell Theory
        2. Levels of Organization
    2. Genetics
        1. DNA
        2. Inheritance

Use exactly four spaces per nesting level, not tabs. The first line is the package title and should not be numbered. Keep sibling numbering local, as in the prompt.

## Build the APK using GitHub Actions (recommended for Termux)
1. Create a new empty GitHub repository, for example `knowledge-library-android`.
2. Upload the contents of this project folder to the repository root (the `.github` folder must be included).
3. Commit and push to the `main` branch.
4. Open the repository on GitHub, then **Actions → Build Knowledge Library APK → Run workflow** (or wait for the first push build).
5. Open the completed workflow run and download the artifact named `knowledge-library-debug-apk`.
6. Extract the ZIP; it contains `app-debug.apk`.
7. Copy the APK to your phone Downloads folder and tap it to install. Android may ask you to allow installs from the app you used to open it.

## Termux notes
This project intentionally uses GitHub Actions so you do not need a full Android SDK installed in Termux. You can upload the project from a computer, or use GitHub's web UI. If using Git in Termux, create a repository, copy these files into it, then `git add . && git commit -m "Initial Knowledge Library app" && git push`.

## Data and limitations
- Imported outlines are saved on this device in the app's WebView local storage. Clearing app storage or uninstalling the app can remove the library; export/backup is not implemented in this first version.
- The app organizes titles and hierarchy from the outline. It does not automatically create full articles, citations, or external databases beyond the imported text.
- This first version accepts plain text files; if a file manager labels a `.txt` file as generic binary, the picker also allows generic text/octet-stream MIME types.
