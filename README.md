# EV Grama Charge

## Problem Statement
The adoption of Electric Vehicles (EVs) in rural and semi-urban areas is hindered by the lack of organized charging infrastructure. Traditional charging stations are often concentrated in urban hubs, leaving EV users in smaller villages and towns with "range anxiety." On the other hand, many local Kirana stores and households have existing electrical setups (5A/15A sockets) that remain underutilized during the day. There is no unified platform to connect EV riders who need a charge with these local micro-hosts who are willing to provide it for a small fee.

## Solution
EV Grama Charge is a decentralized EV charging network that leverages existing local infrastructure to create a peer-to-peer (P2P) charging ecosystem. The application enables local Kirana stores and individuals to register their electrical sockets as charging points. EV riders can locate these nearby sockets via a map, check availability, and pay for the charging duration. This provides an additional income stream for rural shop owners while solving the charging infrastructure gap for EV users.

## Features
- Real-time Map Integration: Locate nearby charging points using OpenStreetMap.
- Dual User Roles: Users can act as either Riders (looking for charge) or Hosts (providing charge).
- Host Management: Kirana store owners can list their sockets (5A/15A), set prices per hour, and manage availability.
- Booking System: Riders can send requests to hosts, and both parties can track the status of the session.
- Charging Calculator: Built-in tool to estimate charging time and cost based on battery capacity and socket type.
- Profile Management: Comprehensive user profiles for both riders and hosts including history and ratings.
- Push Notifications: Real-time alerts for booking requests and status updates.

## Architecture
The project follows Clean Architecture principles combined with the MVVM (Model-View-ViewModel) design pattern to ensure scalability and maintainability.
- Presentation Layer: Built with Jetpack Compose for a modern, reactive UI. ViewModels handle UI logic and state management.
- Domain Layer: Contains the business logic, data models (User, ChargingPoint, Booking), and repository interfaces.
- Data Layer: Implements the repository interfaces, handling data retrieval from Firebase Firestore and Realtime Database, as well as local preferences via DataStore.
- Dependency Injection: Hilt is used to manage dependencies across the layers.

## User Interface (UI)
The UI is designed using Jetpack Compose and Material 3 design systems.
- Navigation: A bottom navigation bar provides quick access to the Map, Calculator, and Profile.
- Map View: Uses osmdroid for an interactive map experience with custom markers for different socket types.
- Onboarding: A dedicated onboarding flow to introduce users to the dual-role ecosystem.
- Auth Screens: Clean, focused interfaces for login and registration with email/password and social integration support.

## Tech Stack
- Language: Kotlin
- UI Framework: Jetpack Compose
- Dependency Injection: Dagger Hilt
- Navigation: Compose Navigation
- Maps: Osmdroid (OpenStreetMap)
- Permissions: Accompanist Permissions
- Image Loading: Coil
- Animations: Lottie
- Data Storage: Jetpack DataStore (Preferences)

## Firebase Integration
Firebase serves as the backend infrastructure for the application:
- Cloud Firestore: Stores structured data for users, charging point details, and booking records.
- Realtime Database: Handles live updates for socket availability and active charging sessions.
- Firebase Messaging (FCM): Enables real-time push notifications for booking communication between riders and hosts.

## Authentication
Authentication is managed via Firebase Auth, providing secure entry points for users. The system supports:
- Email and Password authentication.
- Role-based access control (RBAC) to distinguish between standard riders and host accounts.
- Persistent login sessions managed through Firebase and local DataStore.

## User and Kirana Stores
The platform operates on two primary entities:
- Standard User (Rider): Can search the map, filter by socket type (5A/15A), view host ratings, and manage their charging history.
- Kirana Store (Host): Can register their shop as a charging hub. They provide details such as operating hours, exact location coordinates, socket specifications, and pricing. This empowers small businesses in "Grama" (rural) areas to become part of the green energy revolution.
