import React, { useState, useEffect } from 'react';
import axios from "axios";

const Home = () => {
    const clientId = process.env.REACT_APP_CLIENT_ID;
    const loginUri = `https://dev-80556277.okta.com/oauth2/default/v1/authorize?redirect_uri=http://localhost:3000&response_type=code&state=12345&scope=openid email profile singidunum.read&client_id=${clientId}`;
    const [user, setUser] = useState(null);

    useEffect(() => {
        const fetchUser = async () => {
            const storedUser = localStorage.getItem('session');
            if (storedUser) {
                setUser(JSON.parse(storedUser));
            } else {
                const params = new URLSearchParams(window.location.search);
                const code = params.get('code');
                if (code) {
                    try {
                        const response = await axios.get(`http://localhost:8080/oauth2/callback?code=${code}`);
                        const user = response.data;
                        const accessToken = response.data.access_token
                        localStorage.setItem('session', JSON.stringify(user));
                        localStorage.setItem('access_token', JSON.stringify(accessToken))
                        setUser(user);
                    } catch (error) {
                        console.error('Error fetching token:', error);
                    }
                }
            }
        };

        fetchUser();
    }, []);

    const handleLogin = () => {
        const hardcodedUser = {
            username: 'Zeljko',
            attributes: {
                firstName: 'Zeljko',
                lastName: 'Stojkovic',
                company: 'Yettel',
                userType: 'hardcoded'
            }
        };
        localStorage.setItem('session', JSON.stringify(hardcodedUser));
        setUser(hardcodedUser);
    };

    const handleLogout = () => {
        localStorage.removeItem('session');
        localStorage.removeItem("access_token")
        setUser(null);
    };

    if (!user) {
        return (
            <div className="card">
                <div className="header">
                    <h1>Singidunum</h1>
                </div>
                <div className="content">
                    <p>You are not authenticated.</p>
                </div>
                <footer className="footer">
                    <h3>Login with:</h3>
                    <button onClick={handleLogin}>Hardcoded User</button>
                    <a className="button" href={loginUri}>OAuth2.0</a>
                </footer>
            </div>
        );
    } else {
        return (
            <div className="card">
                <div className="header">
                    <h1>Singidunum</h1>
                </div>
                <div className="content">
                    <h3>Here's what we know about you:</h3>
                    <ul>
                        {Object.entries(user.attributes).map(([key, value]) => (
                            <li key={key}>
                                <strong>{key}:</strong> {value}
                            </li>
                        ))}
                    </ul>
                </div>
                <footer className="footer">
                    <button onClick={handleLogout}>Logout</button>
                    <a href="/exams" className="button">Passed exams</a>
                </footer>
            </div>
        );
    }
};

export default Home;
