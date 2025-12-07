#  Movie Recommendation & Tracker — CPT111 Coursework 3

##  Project Overview
This project is a **Java-based Movie Recommendation and Tracking System** developed for **CPT111 – Programming with Java**.  
It allows users to log in, browse movies, manage a personal watchlist, track history, and receive recommendations — now with a full **JavaFX GUI** interface.

A working **console version** is included, and the GUI version is built on top of the same logic and data.

---

##  Features
- User login (CSV authentication)  
- Browse all movies  
- Add / remove movies in Watchlist  
- Mark movies as watched (History)  
- Recommendation Engine (top-rated unwatched movies)  
- Change password feature  
- GUI with buttons, list view, and dialogs  
- Data stored in CSV and loaded at startup  

 Bonus prototype (not graded):  
A prototype **HTML + CSS + JavaScript** web version is included in *movie-web/*

---

##  Project Structure
```
CPT111-CW3-MovieApp/
│
├─ src/
│ ├─ model/ # Movie & User classes
│ │ ├─ Movie.java
│ │ └─ User.java
│ │
│ ├─ data/ # Handles CSV loading
│ │ ├─ MovieDatabase.java
│ │ └─ UserDatabase.java
│ │
│ ├─ service/ # Recommendation engine logic
│ │ └─ RecommendationEngine.java
│ │
│ ├─ ui/ # JavaFX GUI implementation
│ │ └─ MovieAppFX.java
│ │
│ └─ Main.java # Console-based interface (original version)
│
├─ data/ # CSV movie/user data
│ ├─ movies.csv
│ └─ users.csv
│
└─ movie-web/ # Optional web-based prototype
├─ index.html
├─ style.css
└─ app.js
```

---

##  Technologies Used
| Layer | Technology |
|-------|------------|
| GUI | JavaFX 21 |
| Core Logic | Java (OOP, Collections) |
| Data Handling | CSV File I/O |
| IDE | VS Code |
| Testing | Manual console + GUI testing |

---

##  How to Run (JavaFX GUI)

###  Requirements
- Install **JDK 21 or newer** (compiler + runtime)
- Download **JavaFX SDK 21+** from:  
  --> https://openjfx.io/

Extract JavaFX to a location like:
C:\javafx-sdk-21\

###  Compile
From project root:
```bash
javac --module-path "C:\javafx-sdk-21\lib" --add-modules javafx.controls,javafx.fxml -d out src/model/*.java src/data/*.java src/service/*.java src/ui/MovieAppFX.java
▶ Run Application

java --module-path "C:\javafx-sdk-21\lib" --add-modules javafx.controls,javafx.fxml -cp out ui.MovieAppFX
