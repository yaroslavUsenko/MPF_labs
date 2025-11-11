<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html lang="uk">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>${book.title}</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <style>
        body {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
            padding: 20px 0;
        }
        .container-main {
            background: white;
            border-radius: 10px;
            box-shadow: 0 10px 30px rgba(0,0,0,0.3);
            padding: 30px;
            margin-top: 30px;
        }
        .book-header {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            padding: 20px;
            border-radius: 8px;
            margin-bottom: 30px;
        }
        .book-header h1 {
            margin: 0;
            font-size: 32px;
        }
        .book-header p {
            margin: 10px 0 0 0;
            font-size: 16px;
            opacity: 0.9;
        }
        .comment-item {
            background: #f8f9fa;
            padding: 15px;
            border-radius: 8px;
            border-left: 4px solid #667eea;
            margin-bottom: 10px;
        }
        .comment-author {
            font-weight: bold;
            color: #667eea;
        }
        .comment-text {
            margin: 10px 0;
            color: #333;
            line-height: 1.6;
        }
        .comment-date {
            font-size: 12px;
            color: #999;
            margin-top: 10px;
            display: flex;
            justify-content: space-between;
            align-items: center;
        }
        .btn-custom {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            border: none;
            color: white;
            font-weight: bold;
        }
        .btn-custom:hover {
            background: linear-gradient(135deg, #764ba2 0%, #667eea 100%);
            color: white;
        }
        .btn-danger-custom {
            background: #dc3545;
            padding: 4px 12px;
            font-size: 12px;
        }
        .btn-danger-custom:hover {
            background: #c82333;
        }
        h3 {
            color: #555;
            margin-top: 30px;
            margin-bottom: 20px;
        }
        .form-section {
            background: #f8f9fa;
            border-radius: 8px;
            padding: 20px;
            margin-top: 20px;
        }
        .back-link {
            display: inline-block;
            margin-bottom: 20px;
        }
        .back-link a {
            color: #667eea;
            text-decoration: none;
            font-weight: bold;
        }
        .back-link a:hover {
            text-decoration: underline;
        }
        .alert {
            margin-top: 20px;
        }
    </style>
</head>
<body>
    <div class="container-main">
        <div class="back-link">
            <a href="${pageContext.request.contextPath}/books">← Повернутись до списку</a>
        </div>

        <div class="book-header">
            <h1>📚 ${book.title}</h1>
            <p>✍️ <i>${book.author}</i>, 📅 ${book.pubYear}</p>
        </div>

        <div class="row">
            <div class="col-md-8">
                <h3>💬 Коментарі (${comments.size()})</h3>
                <c:if test="${empty comments}">
                    <div class="alert alert-info">📝 Ще немає коментарів. Будьте першим!</div>
                </c:if>
                <div class="comments-list">
                    <c:forEach var="c" items="${comments}">
                        <div class="comment-item">
                            <div class="comment-author">👤 ${c.author}</div>
                            <div class="comment-text">${c.text}</div>
                            <div class="comment-date">
                                <span>📅 <span class="comment-timestamp" data-instant="${c.createdAt}"></span></span>
                                <form method="post" action="${pageContext.request.contextPath}/comments" style="margin-left: auto;">
                                    <input type="hidden" name="bookId" value="${book.id}">
                                    <input type="hidden" name="commentId" value="${c.id}">
                                    <input type="hidden" name="_method" value="delete">
                                    <button type="submit" class="btn btn-sm btn-danger btn-danger-custom" onclick="return confirm('Видалити коментар?')">🗑️ Видалити</button>
                                </form>
                            </div>
                        </div>
                    </c:forEach>
                </div>
            </div>

            <div class="col-md-4">
                <div class="form-section">
                    <h3>➕ Додати коментар</h3>
                    <form method="post" action="${pageContext.request.contextPath}/comments">
                        <input type="hidden" name="bookId" value="${book.id}">
                        
                        <div class="mb-3">
                            <label for="author" class="form-label">✍️ Ваше ім'я</label>
                            <input type="text" class="form-control" id="author" name="author" 
                                   placeholder="Введіть ваше ім'я" required maxlength="64">
                        </div>
                        
                        <div class="mb-3">
                            <label for="text" class="form-label">💭 Ваш коментар</label>
                            <textarea class="form-control" id="text" name="text" rows="5"
                                      placeholder="Поділіться своєю думкою про книгу..." required maxlength="1000"></textarea>
                            <small class="form-text text-muted">Макс. 1000 символів</small>
                        </div>
                        
                        <button type="submit" class="btn btn-custom w-100">
                            ➕ Додати коментар
                        </button>
                    </form>
                </div>
            </div>
        </div>
    </div>

    <script>
        // Форматуємо java.time.Instant на читаний формат
        document.querySelectorAll('.comment-timestamp').forEach(el => {
            const instant = el.getAttribute('data-instant');
            if (instant) {
                try {
                    const date = new Date(instant);
                    const formatter = new Intl.DateTimeFormat('uk-UA', {
                        year: 'numeric',
                        month: '2-digit',
                        day: '2-digit',
                        hour: '2-digit',
                        minute: '2-digit'
                    });
                    el.textContent = formatter.format(date);
                } catch (e) {
                    el.textContent = instant;
                }
            }
        });
    </script>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>