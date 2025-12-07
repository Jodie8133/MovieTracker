let movies = [];
let currentUser = {
    username: "alice",
    password: "alice123",
    watchlist: ["M008", "M015"],
    history: ["M001", "M011"]
};

async function loadMovies() {
    const res = await fetch('data/movies.json');
    movies = await res.json();
}

let users = [];
async function loadUsers() {
    const res = await fetch('data/users.json');
    users = await res.json();
}

function saveUserData() {
    localStorage.setItem('userData', JSON.stringify(currentUser));
}

function loadUserData() {
    const data = localStorage.getItem('userData');
    if (data) {
        currentUser = JSON.parse(data);
    }
}

function findMovieById(id) {
    return movies.find(m => m.id === id);
}

function init() {
    const loginBtn = document.getElementById('login-btn');
    loginBtn.addEventListener('click', handleLogin);

    document.getElementById('browse-btn').addEventListener('click', browseMovies);
    document.getElementById('watchlist-btn').addEventListener('click', viewWatchlist);
    document.getElementById('history-btn').addEventListener('click', viewHistory);
    document.getElementById('recommend-btn').addEventListener('click', showRecommendations);
    document.getElementById('logout-btn').addEventListener('click', logout);

    loadMovies();
}

function handleLogin() {
    const username = document.getElementById('username').value.trim();
    const password = document.getElementById('password').value.trim();
    const msg = document.getElementById('login-message');

    if (username === currentUser.username && password === currentUser.password) {
        document.getElementById('login-screen').style.display = 'none';
        document.getElementById('main-screen').style.display = 'block';
        document.getElementById('welcome').textContent = `Welcome, ${currentUser.username}`;   
    } else {
        msg.textContent = "Invalid username or password.";
    }
}

function browseMovies() {
    const content = document.getElementById('content');
    content.innerHTML = "<h3>All Movies</h3>";

    if (movies.length === 0) {
        content.innerHTML += "<p>No movies loaded.</p>";
        return;
    }

    let html = "<ul>";
    for (const m of movies) {
        html += `
          <li>
            [${m.id}] ${m.title} (${m.year}) - ${m.genre} - ${m.rating}
            <button class="add-watchlist" data-id="${m.id}">Add to watchlist</button>
            <button class="mark-watched" data-id="${m.id}">Mark watched</button>
          </li>
        `;
    }
    html += "</ul>";

    content.innerHTML = content.innerHTML + html;

    document.querySelectorAll('.add-watchlist').forEach(btn => {
        btn.addEventListener('click', () => {
            const id = btn.dataset.id;
            addToWatchlist(id);
        });
    });

    document.querySelectorAll('.mark-watched').forEach(btn => {
        btn.addEventListener('click', () => {
            const id = btn.dataset.id;
            markAsWatched(id);
        });
    });
}

function addToWatchlist(id) {
    if (!currentUser.watchlist.includes(id)) {
        currentUser.watchlist.push(id);
        alert(`Added ${id} to watchlist.`);
    } else {
        alert(`Movie ${id} is already in your watchlist.`);
    }
}

function removeFromWatchlist(id) {
    const index = currentUser.watchlist.indexOf(id);
    if (index !== -1) {
        currentUser.watchlist.splice(index, 1);
        alert(`Removed ${id} from watchlist.`);
        viewWatchlist();
    }
}

function markAsWatched(id) {
    if (!currentUser.history.includes(id)) {
        currentUser.history.push(id);
    }

    const index = currentUser.watchlist.indexOf(id);
    if (index !== -1) {
        currentUser.watchlist.splice(index, 1);
    }
    alert(`Marked ${id} as watched.`);
}

function viewWatchlist() {
    const content = document.getElementById('content');
    content.innerHTML = "<h3>Your Watchlist</h3>";

    if (currentUser.watchlist.length === 0) {
        content.innerHTML += "<p>Your watchlist is empty.</p>";
        return;
    }

    let html = "<ul>";
    for (const id of currentUser.watchlist) {
        const m = findMovieById(id);
        if (m) {
            html += `
              <li>
                [${m.id}] ${m.title} (${m.year}) - ${m.genre} - ${m.rating}
                <button class="remove-watchlist" data-id="${m.id}">Remove</button>
                <button class="mark-watched" data-id="${m.id}">Mark watched</button>
              </li>
            `;
        } else {
            html += `<li>(Unknown movie ID: ${id})</li>`;
        }
    }
    html += "</ul>";

    content.innerHTML = content.innerHTML + html;

    document.querySelectorAll('.remove-watchlist').forEach(btn => {
        btn.addEventListener('click', () => {
            const id = btn.dataset.id;
            removeFromWatchlist(id);
        });
    });

    document.querySelectorAll('.mark-watched').forEach(btn => {
        btn.addEventListener('click', () => {
            const id = btn.dataset.id;
            markAsWatched(id);
            viewWatchlist(); // refresh the list
        });
    });
}

function viewHistory() {
    const content = document.getElementById('content');
    content.innerHTML = "<h3>Your Viewing History</h3>";

    if (currentUser.history.length === 0) {
        content.innerHTML += "<p>You have not watched any movies yet.</p>";
        return;
    }

    let html = "<ul>";
    for (const id of currentUser.history) {
        const m = findMovieById(id);
        if (m) {
            html += `<li>[${m.id}] ${m.title} (${m.year}) - ${m.genre} - ${m.rating}</li>`;
        } else {
            html += `<li>(Unknown movie ID: ${id})</li>`;
        }
    }
    html += "</ul>";

    content.innerHTML = content.innerHTML + html;
}

function showRecommendations() {
    const content = document.getElementById('content');
    content.innerHTML = "<h3>Recommended Movies</h3>";

    if (movies.length === 0) {
        content.innerHTML += "<p>No movies loaded yet.</p>";
        return;
    }

    const nStr = prompt("How many recommendations would you like?", "5");
    if (nStr === null) return;
    const n = parseInt(nStr, 10);
    if (isNaN(n) || n <= 0) {
        content.innerHTML += "<p>Please enter a valid positive number.</p>";
        return;
    }

    const watchedSet = new Set(currentUser.history);
    const candidates = movies.filter(m => !watchedSet.has(m.id));

    candidates.sort((a, b) => b.rating - a.rating);

    const recs = candidates.slice(0, n);

    if (recs.length === 0) {
        content.innerHTML += "<p>No recommendations available.</p>";
        return;
    }

    let html = "<ul>";
    for (const m of recs) {
        html += `<li>[${m.id}] ${m.title} (${m.year}) - ${m.genre} - ${m.rating}</li>`;
    }
    html += "</ul>";

    content.innerHTML = content.innerHTML + html;
}

function logout() {
    document.getElementById('main-screen').style.display = 'none';
    document.getElementById('login-screen').style.display = 'block';
}

window.addEventListener('DOMContentLoaded', init);