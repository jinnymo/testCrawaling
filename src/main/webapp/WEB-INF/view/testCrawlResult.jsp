<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>크롤링 결과</title>
    <!-- Bootstrap CSS -->
    <link href="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
<div class="container mt-5">
    <h1 class="mb-4">크롤링 결과</h1>

    <table class="table table-bordered">
        <thead class="thead-dark">
        <tr>
            <th>직무</th>
            <th>회사명</th>
            <th>경력</th>
            <th>자격요건</th>
            <th>주요업무</th>
            <th>우대사항</th>
            <th>기술 스택</th>
            <th>마감일</th>
            <th>사이트</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach var="job" items="${jobDataList}">
            <tr>
                <td>${job.title}</td>
                <td>${job.company_name}</td>
                <td>${job.experience}</td>
                <td>
                    <ul>
                        <c:forEach var="qualification" items="${job.qualifications}">
                            <li>${qualification}</li>
                        </c:forEach>
                    </ul>
                </td>
                <td>
                    <ul>
                        <c:forEach var="workInfo" items="${job.work_info}">
                            <li>${workInfo}</li>
                        </c:forEach>
                    </ul>
                </td>
                <td>
                    <ul>
                        <c:forEach var="preferred" items="${job.preferred}">
                            <li>${preferred}</li>
                        </c:forEach>
                    </ul>
                </td>
                <td>
                    <ul>
                        <c:forEach var="skill" items="${job.skills}">
                            <li>${skill}</li>
                        </c:forEach>
                    </ul>
                </td>
                <td>${job.end_date}</td>
                <td>${job.site}</td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
</div>

<!-- Bootstrap JS -->
<script src="https://code.jquery.com/jquery-3.5.1.slim.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@4.5.2/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
