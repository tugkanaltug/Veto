# Project Plan

Veto: A Jetpack Compose call blocker app with Hilt, Layered Architecture (Data, Domain, UI), and UDF. Features include single-screen onboarding (Accompanist), main block list, FAB contact picker, scheduling BottomSheet (7-day picker, time range), Room DB, and a background CallScreeningService for interception and logging. Vibrant M3 design, adaptive icon, and edge-to-edge display. Now adding: Edit schedule on click, delete confirmation dialog, and duplicate number prevention.

## Project Brief

# Project Brief: Veto - Call Blocker

## Features
- **Scheduled Call Screening Service**: A persistent background service utilizing Android\'s CallScreeningService API to monitor and intercept incoming calls based on a Room-backed block list and specific user-defined schedules.
- **Comprehensive Block List Management**: A centralized dashboard featuring logic to prevent duplicate number entries, a delete confirmation dialog to avoid accidental removals, and the ability to edit existing blocking schedules via item clicks.
- **Dynamic Contact & Schedule Configuration**: A searchable contact picker launched via a Floating Action Button (FAB) and an interactive BottomSheet interface for configuring 7-day horizontal day pickers and time-range selections.
- **Integrated Call History Log**: A dedicated navigation destination that provides a historical record of all calls successfully intercepted and rejected by the application.
- **Automated Onboarding Flow**: A single-screen setup process utilizing Accompanist to manage mandatory system permissions, including Call Screening and Contact access, with a vibrant Material 3 design.

## High-Level Technical Stack
- **Kotlin**: The exclusive programming language for the application development.
- **Jetpack Compose & Material 3**: A modern UI toolkit for building a reactive interface with vibrant colors, adaptive icons, and edge-to-edge display support.
- **Hilt**: A dependency injection framework used to manage object lifecycles across the application\'s architecture.
- **Layered Architecture**: Implementation of a structured codebase consisting of Data, Domain, and UI layers following Unidirectional Data Flow (UDF) principles.
- **Room Persistence**: A local database solution for storing blocked contacts and call logs, utilizing **KSP** for code generation.
- **Kotlin Coroutines & Flow**: Used for managing asynchronous operations and facilitating lifecycle-aware UI state collection.
- **Accompanist Permissions**: A specialized library used to streamline the handling and requesting of Android runtime permissions.

## Implementation Steps
**Total Duration:** 1h 1m 22s

### Task_1_Core_Infrastructure: Set up the Room database layer (entities for blocked contacts and logs) and declare the CallScreeningService and BootReceiver in the manifest.
- **Status:** COMPLETED
- **Updates:** Room database (entities and DAOs) and core system components (CallScreeningService and BootReceiver) have been successfully set up. BlockedContact entity includes scheduling fields (startTime, endTime, activeDays). Both the service and receiver are correctly declared in the manifest. Gradle build was successful.
- **Acceptance Criteria:**
  - Room entities and DAOs created
  - CallScreeningService and BootReceiver added to AndroidManifest.xml
  - Project builds successfully
- **Duration:** 9m 56s

### Task_2_Onboarding_And_Navigation: Implement Compose Navigation and a single-screen onboarding process to request Call Screening role and Contacts permissions.
- **Status:** COMPLETED
- **Updates:** Navigation and the onboarding screen have been implemented. The onboarding process successfully requests the Call Screening role and Contacts permission before allowing the user to proceed. The UI follows Material Design 3 and is fully edge-to-edge. Navigation between the Block List and Call Log screens is functional. Project builds successfully.
- **Acceptance Criteria:**
  - Navigation between Block List and Call Log screens works
  - Onboarding screen correctly requests and handles Call Screening role and Contacts permission
  - App navigates to main screen only after permissions are granted
- **Duration:** 2m 12s

### Task_3_Blocked_Contacts_Management: Create the Block List screen, a searchable contact picker via FAB, and a BottomSheet for setting the blocking schedule (7-day picker and time range).
- **Status:** COMPLETED
- **Updates:** Block List screen, searchable contact picker (FAB), and scheduling BottomSheet have been implemented. Users can now pick contacts, set a 7-day schedule with start/end times, and save them to the Room database. The UI follows Material Design 3 and is fully edge-to-edge. Project builds successfully.
- **Acceptance Criteria:**
  - Block List screen displays contacts from Room
  - Searchable contact picker functional
  - BottomSheet correctly captures 7-day schedule and time range and saves to Room
- **Duration:** 1m 32s

### Task_4_Call_Blocking_Logic_And_Log: Implement the call interception logic in CallScreeningService and build the Blocked Calls Log screen to display rejected call history.
- **Status:** COMPLETED
- **Updates:** CallScreeningService and the Blocked Calls Log screen have been implemented. The service now correctly intercepts incoming calls, checks them against the Room database and defined schedules (including overnight ranges), and rejects calls accordingly. Rejected calls are logged in the database and displayed on the new Call Log screen, which includes a 'Clear Logs' feature. The UI is Material 3, vibrant, and edge-to-edge. Gradle build was successful. All tasks are completed.
- **Acceptance Criteria:**
  - CallScreeningService rejects calls based on Room data and schedule
  - Rejected calls are recorded in the Call Log database
  - Blocked Calls Log screen displays history correctly
- **Duration:** 1m 2s

### Task_5_Hilt_And_Architecture_Refactoring: Integrate Hilt for dependency injection and refactor the codebase into Data, Domain, and UI layers following Unidirectional Data Flow (UDF) principles.
- **Status:** COMPLETED
- **Updates:** Hilt dependency injection has been integrated throughout the app. The codebase is now refactored into Data, Domain, and UI layers. VetoViewModel is now a @HiltViewModel and uses StateFlow for UI state management. VetoRepository is injected into the ViewModel and CallScreeningService. Project builds successfully.
- **Acceptance Criteria:**
  - Hilt dependencies added and integrated
  - Codebase structured into Data, Domain, and UI layers
  - ViewModels implement UDF with StateFlow
  - Project builds successfully
- **Duration:** 18m 16s

### Task_6_Polish_And_Verify: Apply vibrant Material 3 theme, implement edge-to-edge display, create an adaptive icon, and perform final verification.
- **Status:** COMPLETED
- **Updates:** Vibrant Material 3 theme and edge-to-edge functional. Adaptive icon created. App does not crash. All existing tests pass and project builds. Final refinement done: dynamicColor is disabled by default to preserve the brand identity, and ic_launcher_round.xml is now an adaptive icon. Project is complete.
- **Acceptance Criteria:**
  - Vibrant Material 3 theme and edge-to-edge functional
  - Adaptive icon created
  - App does not crash
  - All existing tests pass and project builds
- **Duration:** 20m 48s

### Task_7_Enhanced_Management_Features: Implement duplicate number prevention, a delete confirmation dialog, and the ability to edit existing blocking schedules via item clicks.
- **Status:** COMPLETED
- **Updates:** Duplicate number prevention, a delete confirmation dialog, and the ability to edit existing blocking schedules via item clicks have been implemented. Users can now click on a blocked contact to update its schedule, and must confirm before deleting. The app checks for duplicates before adding a new number. Room database correctly updates on edit. Project builds successfully. All tasks in Task 7 are completed.
- **Acceptance Criteria:**
  - Duplicate numbers are prevented from being added to the block list
  - A confirmation dialog appears before deleting a blocked contact
  - Clicking a list item opens the BottomSheet for editing existing schedules
  - Room database updates correctly upon edit
- **Duration:** 7m 36s

### Task_8_Run_And_Verify: Perform a final run and verify application stability, alignment with user requirements, and report critical UI issues.
- **Status:** IN_PROGRESS
- **Acceptance Criteria:**
  - App does not crash
  - All existing tests pass
  - Build pass
  - Duplicate prevention, delete confirmation, and edit schedule functional
- **StartTime:** 2026-03-30 18:03:26 TRT

