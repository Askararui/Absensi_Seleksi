## Tech Stack
- **Bahasa**: Kotlin
- **Architecture**: MVVM
- **Dependency Injection**: Dagger Hilt
- **Database**: Room (mendukung mode offline)
- **API/Networking**: Retrofit & OkHttp (JWT Authentication)
- **Library Lain**: CameraX, Google ML Kit (Face Detection), OsmDroid (Maps)

## Fitur
- Login (JWT)/bisa tanpa memasukkan username & password (dummy)
- Absensi (Foto selfie + deteksi wajah + lokasi GPS)
- Riwayat absensi tersimpan lokal
- Menggunakan Product Flavors (dev & prod)

## Cara Menjalankan
1. Clone repository.
2. Open project di Android Studio.
3. Sync Gradle.
4. Pilih Build Variant (`devDebug` atau `prodDebug`).
5. Jalankan di HP atau emulator (min SDK 26).

## Kredensial Login
- **Username**: admin
- **Password**: admin123
