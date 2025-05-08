✅ Agentic AI Task Checklist for Hotel Reservation System (Kotlin Android MVVM):

📁 Step 1: Set Up Project Structure
Implement exactly this file structure, and create the possible relevant files for code:

hotel_reservation_app/
├── src/
│   └── main/
│       ├── java/
│       │   └── com.example.hotelapp/
│       │       ├── activity/
│       │       │   ├── SplashActivity.kt
│       │       │   ├── MainActivity.kt
│       │       │   ├── LoginActivity.kt
│       │       │   └── RegisterActivity.kt
│       │       │
│       │       ├── fragments/
│       │       │   ├── HomeFragment.kt
│       │       │   ├── BookingFragment.kt
│       │       │   ├── RoomDetailsFragment.kt
│       │       │   ├── PaymentFragment.kt
│       │       │   ├── ProfileFragment.kt
│       │       │   ├── AdminDashboardFragment.kt
│       │       │   ├── ManageRoomsFragment.kt
│       │       │   └── ReviewsFragment.kt
│       │       │
│       │       ├── adapters/
│       │       │   ├── RoomAdapter.kt
│       │       │   ├── AmenityAdapter.kt
│       │       │   ├── BookingAdapter.kt
│       │       │   └── ReviewAdapter.kt
│       │       │
│       │       ├── model/
│       │       │   ├── User.kt
│       │       │   ├── Guest.kt
│       │       │   ├── Admin.kt
│       │       │   ├── Room.kt
│       │       │   ├── RoomType.kt
│       │       │   ├── Amenity.kt
│       │       │   ├── Booking.kt
│       │       │   ├── Payment.kt
│       │       │   ├── Review.kt
│       │       │   ├── Recommendation.kt
│       │       │   ├── Service.kt
│       │       │   └── Image.kt
│       │       │
│       │       ├── network/
│       │       │   ├── ApiClient.kt
│       │       │   ├── ApiService.kt
│       │       │   └── NetworkInterceptor.kt
│       │       │
│       │       ├── repository/
│       │       │   ├── UserRepository.kt
│       │       │   ├── BookingRepository.kt
│       │       │   ├── RoomRepository.kt
│       │       │   └── ReviewRepository.kt
│       │       │
│       │       ├── viewmodel/
│       │       │   ├── UserViewModel.kt
│       │       │   ├── BookingViewModel.kt
│       │       │   ├── RoomViewModel.kt
│       │       │   └── ReviewViewModel.kt
│       │       │
│       │       └── utils/
│       │           ├── Constants.kt
│       │           ├── Extensions.kt
│       │           ├── SessionManager.kt
│       │           └── ImageUploader.kt
│       │
│       └── res/
│           ├── layout/
│           │   ├── activity_main.xml
│           │   ├── fragment_home.xml
│           │   ├── fragment_booking.xml
│           │   ├── fragment_payment.xml
│           │   ├── fragment_room_details.xml
│           │   └── item_room.xml
│           │
│           ├── navigation/
│           │   └── nav_graph.xml
│           │
│           ├── drawable/
│           │   └── (app logos, images, icons)
│           │
│           └── values/
│               ├── colors.xml
│               ├── strings.xml
│               └── styles.xml

 Create all specified folders exactly as above.

🎨 Step 2: Define Data Models (matching provided Database schema)
Create Kotlin data classes in model/ folder:

 User.kt

 Guest.kt

 Admin.kt

 AdminAction.kt

 Room.kt

 RoomType.kt

 Amenity.kt

 Booking.kt

 Payment.kt

 Service.kt

 Recommendation.kt

 Image.kt

 Review.kt

(Each class aligns exactly with database schema provided by user.)

📡 Step 3: Setup Retrofit (ExpressJS Backend Integration but backend code is subject to change)
Inside network/:

 ApiClient.kt (Retrofit singleton instance)

 ApiService.kt (interface methods matching backend API)

 NetworkInterceptor.kt (for adding headers or logging)

 Endpoints will come soon for those are still being converted saving into mongodb.

🔐 Step 4: Create Repository Layer (Data Management)
Inside repository/:

 UserRepository.kt

 RoomRepository.kt

 BookingRepository.kt

 ReviewRepository.kt

🎯 Step 6: Implement ViewModel Logic
Inside viewmodel/:

 UserViewModel.kt

 RoomViewModel.kt

 BookingViewModel.kt

 ReviewViewModel.kt

(ViewModels manage UI data and handle API calls via repositories.)

📱 Step 7: Create Activities and Navigation
Inside activity/:

 SplashActivity.kt (App loading screen)

 MainActivity.kt (App navigation host)

 LoginActivity.kt

 RegisterActivity.kt

Inside navigation/ (Navigation Graph):

 Create navigation graph XML (nav_graph.xml).

🖼️ Step 8: Develop Fragments (User Interface)
Inside fragments/:

 HomeFragment.kt (List of available rooms)

 BookingFragment.kt (Booking functionality)

 RoomDetailsFragment.kt (Individual room details)

 PaymentFragment.kt (Payment processing)

 ProfileFragment.kt (User Profile management)

 AdminDashboardFragment.kt (Admin functionalities)

 ManageRoomsFragment.kt (Room CRUD operations by Admin)

 ReviewsFragment.kt (User-generated reviews)

📝 Step 9: RecyclerView Adapters
Inside adapters/:

 RoomAdapter.kt

 AmenityAdapter.kt

 BookingAdapter.kt

 ReviewAdapter.kt

🛠️ Step 10: Utilities and Helpers
Inside utils/:

 Constants.kt (API URLs, keys, shared constants)

 Extensions.kt (Kotlin extensions)

 SessionManager.kt (Manage user authentication tokens, preferences)

 ImageUploader.kt (Cloudinary image uploading integration)

🗃️ Step 11: XML UI Layouts
Inside res/layout/:

 activity_main.xml

 activity_login.xml

 activity_register.xml

 fragment_home.xml

 fragment_booking.xml

 fragment_payment.xml

 fragment_room_details.xml

 item_room.xml

 item_review.xml

📤 Step 12: Implement Image Management (Cloudinary Integration)
 Implement Cloudinary SDK/API within ImageUploader.kt.

 Integrate upload and deletion functionality.

🔎 Step 13: Testing and Debugging
 Write unit tests for ViewModels and Repositories.

 Ensure integration with ExpressJS backend is successful.

 Check UI/UX flows (Registration, Login, Booking, Payment).

🚀 Step 14: Deployment Preparation
 Optimize code and dependencies.

 ProGuard/R8 rules for APK size and security.

 Prepare for release build (signing keys, etc.).

📌 Additional Instructions for Agentic AI:
Read carefully the provided schema entities and attributes.

Ensure the database attributes match Kotlin data models exactly.

Verify correct handling of Foreign Key relationships.

Keep consistent naming conventions and Kotlin best practices.

Update checkbox status ([x]) immediately after successfully completing each task.