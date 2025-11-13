<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="uk">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <title>Каталог книг</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.0/font/bootstrap-icons.css" rel="stylesheet">
        <style>
            * {
                margin: 0;
                padding: 0;
                box-sizing: border-box;
            }
            
            body {
                background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                min-height: 100vh;
                padding: 30px 0;
                font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            }
            
            .container-main {
                background: white;
                border-radius: 12px;
                box-shadow: 0 15px 40px rgba(0,0,0,0.25);
                padding: 40px;
                margin: 0 auto;
                max-width: 1100px;
            }
            
            .header-section {
                text-align: center;
                margin-bottom: 40px;
            }
            
            h1 {
                color: #333;
                font-size: 2.5em;
                font-weight: 700;
                margin-bottom: 10px;
                background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                -webkit-background-clip: text;
                -webkit-text-fill-color: transparent;
                background-clip: text;
            }
            
            .header-subtitle {
                color: #999;
                font-size: 1.1em;
                margin-bottom: 20px;
            }
            
            .separator {
                width: 80px;
                height: 4px;
                background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                margin: 0 auto 30px;
                border-radius: 2px;
            }
            
            .main-content {
                display: grid;
                grid-template-columns: 1fr 380px;
                gap: 30px;
            }
            
            h3 {
                color: #333;
                font-weight: 600;
                margin-bottom: 20px;
                font-size: 1.3em;
                display: flex;
                align-items: center;
                gap: 10px;
            }
            
            .books-list {
                display: flex;
                flex-direction: column;
                gap: 12px;
            }
            
            .book-item {
                background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                color: white;
                padding: 18px;
                border-radius: 10px;
                text-decoration: none;
                transition: all 0.3s ease;
                border: 2px solid transparent;
                box-shadow: 0 4px 12px rgba(102, 126, 234, 0.2);
            }
            
            .book-item:hover {
                transform: translateY(-5px);
                box-shadow: 0 10px 25px rgba(102, 126, 234, 0.4);
                border-color: rgba(255,255,255,0.3);
                color: white;
                text-decoration: none;
            }
            
            .book-item-title {
                font-weight: 700;
                font-size: 18px;
                margin-bottom: 8px;
                display: flex;
                align-items: center;
                gap: 10px;
            }
            
            .book-item-author {
                font-size: 14px;
                opacity: 0.95;
            }
            
            .empty-message {
                background: #e7f3ff;
                border-left: 4px solid #667eea;
                padding: 15px;
                border-radius: 6px;
                color: #0066cc;
            }
            
            .form-section {
                background: linear-gradient(135deg, #f5f7fa 0%, #f0f2f5 100%);
                border-radius: 12px;
                padding: 25px;
                border: 1px solid #e0e0e0;
                position: sticky;
                top: 30px;
            }
            
            .form-section h3 {
                color: #333;
                margin-bottom: 20px;
                font-size: 1.1em;
            }
            
            .form-label {
                color: #555;
                font-weight: 600;
                margin-bottom: 8px;
                font-size: 0.95em;
            }
            
            .form-control {
                border: 2px solid #e0e0e0;
                border-radius: 8px;
                padding: 10px 12px;
                font-size: 0.95em;
                transition: all 0.3s ease;
                margin-bottom: 15px;
            }
            
            .form-control:focus {
                border-color: #667eea;
                box-shadow: 0 0 0 0.2rem rgba(102, 126, 234, 0.15);
                outline: none;
            }
            
            .form-control::placeholder {
                color: #ccc;
            }
            
            .form-text {
                font-size: 0.85em;
                color: #999;
                margin-top: -10px;
                margin-bottom: 12px;
            }
            
            .btn-custom {
                background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                border: none;
                color: white;
                font-weight: 600;
                padding: 12px 20px;
                border-radius: 8px;
                transition: all 0.3s ease;
                cursor: pointer;
                font-size: 0.95em;
                width: 100%;
            }
            
            .btn-custom:hover {
                background: linear-gradient(135deg, #764ba2 0%, #667eea 100%);
                color: white;
                transform: translateY(-2px);
                box-shadow: 0 8px 16px rgba(102, 126, 234, 0.3);
            }
            
            .btn-custom:active {
                transform: translateY(0);
            }
            
            /* Responsive */
            @media (max-width: 768px) {
                .main-content {
                    grid-template-columns: 1fr;
                }
                
                .form-section {
                    position: static;
                }
                
                h1 {
                    font-size: 1.8em;
                }
                
                .container-main {
                    padding: 20px;
                }
            }
        </style>
    </head>
    <body>
        <div class="container-main">
            <div class="header-section">
                <h1>📚 Каталог книг</h1>
                <p class="header-subtitle">Збірка цікавих творів світової та вітчизняної літератури</p>
                <div class="separator"></div>
            </div>
            
            <div class="main-content">
                <div>
                    <h3><i class="bi bi-book"></i> Список книг</h3>
                    <jsp:useBean id="books" scope="request" type="java.util.List"/>
                    <c:if test="${empty books}">
                        <div class="empty-message">
                            <strong>📖 Библіотека порожня</strong><br>
                            Додайте першу книгу в каталог, використовуючи форму поруч!
                        </div>
                    </c:if>
                    <div style="margin-bottom: 16px;">
                        <form method="get" action="${pageContext.request.contextPath}/books" class="d-flex" role="search">
                            <input type="text" name="q" value="${q}" class="form-control" placeholder="Пошук за назвою або автором" />
                            <input type="hidden" name="page" value="${page}" />
                            <input type="hidden" name="size" value="${size}" />
                            <button type="submit" class="btn btn-custom" style="margin-left:8px;">🔎 Пошук</button>
                        </form>
                    </div>

                    <div class="books-list">
                        <c:forEach var="book" items="${books}">
                            <a href="${pageContext.request.contextPath}/comments?bookId=${book.id}" class="book-item">
                                <div class="book-item-title">
                                    <i class="bi bi-book-half"></i> ${book.title}
                                </div>
                                <div class="book-item-author">
                                    ✍️ <i>${book.author}</i> — ${book.pubYear}
                                </div>
                            </a>
                        </c:forEach>
                    </div>

                        <c:if test="${totalPages > 1}">
                            <nav aria-label="Page navigation" style="margin-top:16px;">
                                <ul class="pagination">
                                    <c:forEach var="p" begin="0" end="${totalPages - 1}">
                                        <li class="page-item ${p == page ? 'active' : ''}">
                                            <a class="page-link" href="${pageContext.request.contextPath}/books?page=${p}&size=${size}&q=${q}">${p + 1}</a>
                                        </li>
                                    </c:forEach>
                                </ul>
                            </nav>
                        </c:if>
                </div>

                <div>
                    <div class="form-section">
                        <h3>➕ Додати нову книгу</h3>
                        <form method="post" action="${pageContext.request.contextPath}/books">
                            <div>
                                <label for="title" class="form-label">📖 Назва книги</label>
                                <input type="text" class="form-control" id="title" name="title" 
                                       placeholder="Наприклад: Кобзар" required>
                            </div>
                            
                            <div>
                                <label for="author" class="form-label">✍️ Автор</label>
                                <input type="text" class="form-control" id="author" name="author" 
                                       placeholder="Наприклад: Шевченко" required>
                            </div>
                            
                            <div>
                                <label for="pubDate" class="form-label">📅 Дата публікації</label>
                                <input type="date" class="form-control" id="pubDate" name="pubDate" 
                                       required>
                                <small class="form-text">Рік книги буде автоматично витягнутий з дати</small>
                            </div>
                            
                            <button type="submit" class="btn btn-custom">
                                ➕ Додати книгу
                            </button>
                        </form>
                    </div>
                </div>
            </div>
        </div>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    </body>
</html>