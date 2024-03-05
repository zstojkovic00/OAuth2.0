import React, {useState, useEffect} from 'react';

const Home = () => {

    const clientId = process.env.REACT_APP_CLIENT_ID;
    console.log(process.env.REACT_APP_CLIENT_ID);

    const loginUri = `https://dev-80556277.okta.com/oauth2/default/v1/authorize?redirect_uri=http://localhost:8080/oauth2/callback&response_type=code&state=12345&scope=openid email profile&client_id=${clientId}`;

    const [user, setUser] = useState(null);

    useEffect(() => {
        const session = JSON.parse(localStorage.getItem('session'));
        if (session && session.username) {
            setUser(session);
        }
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
                    <a className="button" href={loginUri}> OAuth2.0</a>
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
