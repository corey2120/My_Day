# Google Calendar Sync Setup Guide

## Overview
MyDay can now sync with Google Calendar to import your calendar events as tasks!

## Important Note
Google Calendar integration requires additional setup with Google Cloud Console. This is a multi-step process that involves:

1. Creating a Google Cloud Project
2. Enabling the Google Calendar API
3. Creating OAuth 2.0 credentials
4. Adding your app's SHA-1 fingerprint

## Setup Steps

### 1. Create Google Cloud Project
1. Go to https://console.cloud.google.com/
2. Click "Create Project"
3. Name it "MyDay Calendar Sync"
4. Click "Create"

### 2. Enable Google Calendar API
1. In your project, go to "APIs & Services" → "Library"
2. Search for "Google Calendar API"
3. Click on it and click "Enable"

### 3. Create OAuth 2.0 Credentials
1. Go to "APIs & Services" → "Credentials"
2. Click "Create Credentials" → "OAuth 2.0 Client ID"
3. Select "Android"
4. Name: "MyDay Android App"
5. Package name: `com.example.myday`
6. Get your SHA-1 fingerprint:
   ```bash
   cd /home/cobrien/StudioProjects/MyDay
   keytool -list -v -keystore ~/.android/debug.keystore -alias androiddebugkey -storepass android -keypass android
   ```
7. Copy the SHA-1 fingerprint and paste it in the Google Cloud Console
8. Click "Create"

### 4. Test Calendar Permissions
1. Open MyDay app
2. Go to Settings
3. Find "Google Calendar Sync"
4. Click "Sign in with Google"
5. Select your Google account
6. Grant calendar permissions
7. Click "Sync Now" to import events

## Alternative: Use Device Calendar
For a simpler solution without Google Cloud setup, the app already has READ_CALENDAR and WRITE_CALENDAR permissions. You could access the device's local calendar provider directly without OAuth.

## Features Once Set Up
- ✅ One-click sync of upcoming events
- ✅ Events appear as tasks in MyDay
- ✅ Events show on the calendar view
- ✅ Sync up to 30 days ahead
- ✅ Avoid duplicate imports

## Current Status
- Code is implemented and builds successfully
- Requires OAuth 2.0 configuration to function
- Alternative: Consider using Android's ContentProvider for device calendar access

---

Would you like me to implement a simpler device calendar sync instead that doesn't require Google Cloud Console setup?
