# 📍 Euronavigate – Location Tracker Android App

A GPS location tracking Android app developed using **Jetpack Compose** and **Google Maps SDK**. It
tracks user location in real time, visualizes it with pins and polylines, and provides detailed
stats and export options.

---

## ✨ Features

### 🗺 Map Screen

- Live GPS tracking with pins dropped at each recorded location
- Polyline drawn between locations to visualize the path
- Tap any pin to view location details in a bottom sheet
- Zoom controls (+/-)
- "Fit Pins" to zoom out and fit all markers
- Floating button to recenter to current location
- Smooth camera tracking to newly added points

### 🔁 Location Tracking

- Start and stop tracking with one tap
- Interval controlled via Settings

### ⚙️ Settings Screen

- Set location update interval (in minutes)
- Export collected data as JSON using Android’s native share sheet
- View statistics like:
    - Total distance
    - Average / Min / Max speed
    - Last altitude

### 🔄 Extras

- Pager view showing all recorded points (select to center map)
- Highlighted pins with aura when selected
- Responsive UI across all screen sizes
- Smooth camera handling and transitions

---

## 🧱 Tech Stack

| Tool                        | Purpose                          |
|-----------------------------|----------------------------------|
| **Kotlin**                  | Primary language                 |
| **Jetpack Compose**         | UI                               |
| **Google Maps SDK**         | Location and map UI              |
| **Fused Location Provider** | GPS tracking                     |
| **ViewModel + StateFlow**   | State management                 |
| **FileProvider**            | Secure sharing of exported files |
| **Repository Pattern**      | Clean architecture               |

---

## 🧪 How to Run

1. Clone the repository:
   ```bash
   git clone https://github.com/Luk4s2/euronavigate.git
   ```
2. Open in **Android Studio Arctic Fox+**
3. Enable location permissions in emulator/device
4. Run the app and start tracking!

---

## 📤 Export & Share

- Go to **Settings**
- Tap "Export"
- The app will generate a `.json` file containing all recorded GPS points
- Share it using email, Google Drive, or any supported app

---

## 🧠 Developer Notes

- This app is modular, testable, and built following Android architecture guidelines
- It can be extended easily with analytics, background tracking, or cloud sync

---

## 📃 License

MIT License — free to use, modify, and contribute
