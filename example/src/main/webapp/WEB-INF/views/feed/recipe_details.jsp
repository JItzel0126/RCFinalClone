<%--
  Created by IntelliJ IDEA.
  User: user
  Date: 25. 9. 12.
  Time: 오전 11:21
  To change this template use File | Settings | File Templates.
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<%-- 날짜 포맷을 쓰고 싶으면 fmt를 추가하고, LocalDateTime -> String 변환은 컨버터/DTO에서 처리 권장
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
--%>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title><c:out value="${recipe.recipeTitle}"/> - Details</title>
    <c:set var="ctx" value="${pageContext.request.contextPath}" />
    <link rel="stylesheet" href="${ctx}/css/common.css" />
    <link rel="stylesheet" href="${ctx}/css/recipe-details.css" />
</head>
<body>
<div class="container" data-recipe-uuid="${recipe.uuid}">
    <!-- 헤더 -->
    <header class="container">
        <div class="flex-box">
            <div class="flex-row">
                <h1 class="page-title">Details</h1>
                <a href="${ctx}/" class="float-text">home</a>
            </div>

            <div class="header-actions">
                <a class="register">👤</a>
                <div class="notif-wrap">
                    <button id="btnNotif" class="notif-btn" aria-haspopup="dialog" aria-expanded="false"
                            aria-controls="notifPanel" title="알림">🔔
                        <span class="notif-dot" aria-hidden="true"></span>
                    </button>
                    <div id="notifPanel" class="notif-panel" role="dialog" aria-label="알림 목록">
                        <div class="notif-head">
                            <strong>알림</strong>
                            <div class="actions"><button class="btn small ghost" id="markAll">모두 읽음</button></div>
                        </div>
                        <div class="notif-list" id="notifList"></div>
                        <div class="notif-foot"><button class="btn small ghost" id="closeNotif">닫기</button></div>
                    </div>
                </div>
            </div>
        </div>
    </header>

    <!-- MAIN -->
    <div class="layout">
        <main>
            <!-- 메인 콘텐츠 -->
            <section class="contentBar content mb-12">
                <div class="mb-12">
                    <h1 class="title">
                        <c:out value="${recipe.recipeTitle}" />
                    </h1>
                    <div class="meta">
                        조회수 <c:out value="${recipe.viewCount}" />회 ·
                        <c:out value="${insertTime}" /> 업로드 ·
                        조리시간 <c:out value="${recipe.cookingTime}" />분
                    </div>
                </div>

                <!-- 이미지/텍스트 슬라이드 -->
                <div class="step-slider mb-12">
                    <div class="slides" id="imgSlides">
                        <c:choose>
                            <c:when test="${not empty recipe.contents}">
                                <c:forEach var="c" items="${recipe.contents}">
                                    <c:set var="imgSrc" value="${c.recipeImageUrl}"/>
                                    <c:if test="${fn:startsWith(imgSrc, '/')}">
                                        <c:set var="imgSrc" value="${ctx}${imgSrc}"/>
                                    </c:if>
                                    <div class="slide"><img src="${imgSrc}" alt="" /></div>
                                </c:forEach>
<%--                                <c:forEach var="c" items="${recipe.contents}">--%>
<%--                                    <div class="slide">--%>
<%--                                        <img src="<c:out value='${c.recipeImageUrl}'/>" alt="" />--%>
<%--                                    </div>--%>
<%--                                </c:forEach>--%>
                            </c:when>
                            <c:otherwise>
                                <!-- 컨텐츠 이미지 없으면 썸네일/플레이스홀더 -->
                                <div class="slide">
                                    <img src="<c:out value='${empty recipe.thumbnailUrl ? (ctx += "https://placehold.co/600x400") : recipe.thumbnailUrl}'/>" alt="" />
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </div>

                    <button class="prev" type="button" aria-label="이전">◀</button>
                    <button class="next" type="button" aria-label="다음">▶</button>
                </div>

                <aside class="panel mb-12">
                    <h3>👣 조리 순서</h3>
                    <div id="textPanel">
                        <div class="text-viewport">
                            <div class="slides" id="textSlides">
                                <c:choose>
                                    <c:when test="${not empty recipe.contents}">
                                        <c:forEach var="c" items="${recipe.contents}">
                                            <div class="slide">
                                                <p><c:out value="${c.stepExplain}" /></p>
                                            </div>
                                        </c:forEach>
                                    </c:when>
                                    <c:otherwise>
                                        <div class="slide"><p>등록된 조리 단계가 없습니다.</p></div>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                        </div>
                    </div>
                </aside>

                <aside class="panel">
                    <h3>🧾 재료</h3>
                    <ul class="grid">
                        <c:choose>
                            <c:when test="${not empty recipe.ingredients}">
                                <c:forEach var="ing" items="${recipe.ingredients}">
                                    <li>
                                        <c:out value="${ing.ingredientName}" />
                                        <c:if test="${not empty ing.ingredientAmount}"> <c:out value="${ing.ingredientAmount}" /></c:if>
                                    </li>
                                </c:forEach>
                            </c:when>
                            <c:otherwise>
                                <li>등록된 재료가 없습니다.</li>
                            </c:otherwise>
                        </c:choose>
                    </ul>
                </aside>
            </section>

            <!-- 채널바 / 액션바 -->
            <section class="channelBar">
                <div class="channel mb-8">
                    <div class="channelInfo">
                        <img class="avatar" src="${ctx}/images/avatar-placeholder.png" alt="" />
                        <div>
                            <strong><c:out value="${recipe.userId}" /></strong>
                            <div class="meta">
                                <c:out value="${recipe.recipeCategory}" /> ·
                                <c:out value="${recipe.difficulty}" />
                            </div>
                        </div>
                    </div>
                    <a class="followbtn-sm" id="btnFollow">Follow</a>
                </div>

                <div class="actions">
                    <button class="btn-none red" id="btnLike">👍 좋아요 <span id="likeCnt"><c:out value="${recipe.likeCount}" /></span></button>
                    <button class="btn-none" id="btnShare">🔗 공유</button>
                    <button class="btn-none" id="btnReport">🚩 신고</button>
                </div>
            </section>

            <!-- 본문/태그 -->
            <section class="desc" id="postDesc">
                <div class="tags">
                    <c:forEach var="t" items="${recipe.tags}">
                        <span class="tag">#<c:out value="${t.tag}" /></span>
                    </c:forEach>
                </div>
                <div class="contentText"><c:out value="${recipe.recipeIntro}" /></div>
                <button type="button" class="btn-none toggle" id="btnToggleDesc">더보기</button>
            </section>

            <!-- 댓글 (AJAX 예정) -->
            <section class="comments" id="comments">
                <h3 class="comments-title">💬 댓글 <span class="count"><c:out value="${recipe.commentCount}" /></span></h3>
                <div class="comment-input">
                    <img class="avatar-sm" src="${ctx}/images/avatar-placeholder.png" alt="" />
                    <label class="sr-only" for="cmt"></label>
                    <textarea id="cmt" placeholder="따끈한 피드백 남기기..."></textarea>
                    <button type="button" class="btn" id="btnCmtSubmit">등록</button>
                </div>

                <!-- 목록은 추후 AJAX로 -->
                <div class="comments-box" data-show-count="3">
                    <div class="list" id="cmtList"></div>
                    <button type="button" class="toggle toggleBox" id="btnCmtMore">더보기</button>
                </div>
            </section>
        </main>

        <!-- 사이드 추천 (옵션) -->
        <aside class="side" id="sideList" aria-live="polite">
            <div class="loader" id="loader" hidden>
                <div class="spinner"></div>
                더 불러오는 중…
            </div>
            <div id="sentinel" style="height: 1px"></div>
        </aside>
    </div>
</div>

<script src="${ctx}/js/recipe-detail-common.js"></script>
<script src="${ctx}/js/recipe-details.js"></script>
</body>
</html>
