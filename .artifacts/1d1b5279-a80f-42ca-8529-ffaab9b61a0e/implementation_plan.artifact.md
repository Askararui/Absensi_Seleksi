# Rencana Implementasi Absensi Seleksi

Rencana ini mencakup penyiapan arsitektur MVVM, integrasi library pihak ketiga (Camera, Maps, Face Detection), sistem autentikasi JWT, mode offline, dan konfigurasi Product Flavors.

## User Review Required

> [!IMPORTANT]
> Saya menyarankan penggunaan **Dagger Hilt** untuk Dependency Injection agar arsitektur MVVM lebih bersih dan mudah dikelola. Mohon konfirmasi jika Anda lebih memilih library lain (seperti Koin) atau tanpa DI library.

> [!NOTE]
> Untuk Product Flavor, saya akan membuat dua varian: `dev` (untuk pengembangan) dan `prod` (untuk rilis).

## Proposed Changes

### 1. Build Configuration & Dependencies
Pembaruan `libs.versions.toml` dan `build.gradle.kts` untuk menyertakan semua library yang dibutuhkan.

#### [MODIFY] [libs.versions.toml](file:///D:/AbsensiSeleksi/gradle/libs.versions.toml)
- Menambahkan versi dan definisi library untuk: Retrofit, Room, CameraX, Osmdroid, ML Kit, dan Hilt.

#### [MODIFY] [app/build.gradle.kts](file:///D:/AbsensiSeleksi/app/build.gradle.kts)
- Menerapkan plugin Hilt dan Room.
- Mengonfigurasi `productFlavors` (`dev` & `prod`).
- Menambahkan dependensi ke blok `dependencies`.

### 2. Project Structure (MVVM)
Penyiapan folder/package untuk memisahkan tanggung jawab kode.

#### [NEW] Folder Structure
- `com.absensiseleksi.data`: Untuk `remote` (API), `local` (Room), dan `repository`.
- `com.absensiseleksi.di`: Untuk modul Hilt.
- `com.absensiseleksi.ui`: Untuk `view` (Activity/Fragment) dan `viewmodel`.
- `com.absensiseleksi.utils`: Untuk helper (JWT Interceptor, CameraUtils, dll).

### 3. Core Features Implementation
- **Authentication JWT**: Membuat `AuthInterceptor` untuk OkHttp guna menyisipkan token secara otomatis.
- **Offline Mode**: Implementasi `Room Database` sebagai cache data lokal.
- **Camera & Face Detection**: Integrasi `CameraX` dengan `ML Kit Face Detection`.
- **OpenStreetMap**: Integrasi `osmdroid` untuk fitur peta.

## Verification Plan

### Automated Tests
- Menjalankan unit test untuk `Repository` dan `ViewModel`.
- Verifikasi build untuk setiap `productFlavor`.

### Manual Verification
- Uji coba login (JWT) dan akses kamera.
- Uji coba deteksi wajah pada preview kamera.
- Uji coba peta OpenStreetMap.
- Uji coba mode offline dengan mematikan koneksi internet.
