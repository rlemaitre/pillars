<!doctype html>
<html lang="en">
<%include "header.gsp"%>
<body>
<%include "page_menu.gsp"%>
<div class="wrapper">
    <aside class="doc__nav"></aside>
    <article class="doc__content">
        <h3>Search Results</h3>
        <script src="${content.rootpath}js/lunr.js"></script>
        <script src="${content.rootpath}lunrjsindex.js"></script>
        <script src="${content.rootpath}js/lunrsearch.js"></script>
        <script src="${content.rootpath}js/lunrdosearch.js"></script>
        <input type="text" name="q" id="lunrsrc" onkeyup="dosearch(this);" onchange="dosearch(this);"/>

        <div id="results"></div>
        <script>
            const input = document.querySelector("#lunrsrc");
            input.focus();
            const params = new URLSearchParams(window.location.search);
            input.value = params.get('q');
            dosearch(input);
        </script>
    </article>
    <aside class="doc__nav pop-out"></aside>
</div>
<%include "footer.gsp"%>
</body>
</html>
