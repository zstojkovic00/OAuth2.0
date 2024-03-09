import React, { useState, useEffect } from 'react';
import axios from 'axios';

const Exam = () => {

    const [exams, setExams] = useState([]);

    useEffect(() => {
        const fetchExams = async () => {
            try {
                const storedAccessToken = localStorage.getItem('access_token');
                if (!storedAccessToken) {
                    console.error('Access token not found in local storage');
                    return;
                }

                console.log(storedAccessToken);

                const response = await axios.get('http://localhost:8080/exams', {
                    headers: {
                        'Authorization': `Bearer ${storedAccessToken}`
                    }
                });
                setExams(response.data);
                console.log(response.data);

            } catch (error) {
                console.error('Error fetching exams:', error);
            }
        };

        fetchExams();
    }, []);

    return (
        <div className="card">
            <div className="header">
                <h1>Exams</h1>
            </div>
            <div className="content">
                <table>
                    <thead>
                    <tr>
                        <th>Code</th>
                        <th>Name</th>
                        <th>Points</th>
                        <th>Grade</th>
                        <th>Date</th>
                        <th>Professor</th>
                    </tr>
                    </thead>
                    <tbody>
                    {exams.length > 0 ? (
                        exams.map((exam, index) => (
                            <tr key={index}>
                                <td>{exam.code}</td>
                                <td>{exam.name}</td>
                                <td>{exam.points}</td>
                                <td>{exam.grade}</td>
                                <td>{exam.date}</td>
                                <td>{exam.professor}</td>
                            </tr>
                        ))
                    ) : (
                        <tr>
                            <td colSpan="6">No exams found</td>
                        </tr>
                    )}
                    </tbody>
                </table>
            </div>
            <div className="footer">
                <a href="/" className="button">Back</a>
            </div>
        </div>
    );
};

export default Exam;
