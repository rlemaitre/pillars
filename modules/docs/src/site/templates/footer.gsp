<footer class="footer">
    <div class="row">
        <% if (config.site_footerGithub) { %>
        <a href="${config.site_footerGithub}" rel="noopener noreferrer" title="Twitter"><i class="fa fa-github"></i></a>
        <% } %>
        <% if (config.site_footerTwitter) { %>
        <a href="${config.site_footerTwitter}" rel="noopener noreferrer" title="Twitter"><i class="fa fa-twitter"></i></a>
        <% } %>
        <% if (config.site_footerSO) { %>
        <a href="${config.site_footerSO}" rel="noopener noreferrer" title="Twitter"><i class="fa fa-stack-overflow"></i></a>
        <% } %>
        <% if (config.site_footerSlack) { %>
        <a href="${config.site_footerSlack}" rel="noopener noreferrer" title="Twitter"><i class="fa fa-slack"></i></a>
        <% } %>
        <% if (config.site_footerMail) { %>
        <a href="mailto:${config.site_footerMail}" rel="noopener noreferrer" target="_blank" title="Email"><i class="fa fa-envelope"></i></a>
        <% } %>
    </div>
    <div class="row">${config.site_footerText}</div>
</footer>
<script src="${content.rootpath}js/highlight.min.js"></script>
<script src="${content.rootpath}js/languages/dockerfile.min.js"></script>
<script src="${content.rootpath}js/languages/gradle.min.js"></script>
<script src="${content.rootpath}js/languages/scala.min.js"></script>
<script src="${content.rootpath}js/languages/sql.min.js"></script>
<script src="${content.rootpath}js/languages/yaml.min.js"></script>
<script src="${content.rootpath}js/languages/xml.min.js"></script>
<script>hljs.highlightAll();</script>
<script src="${content.rootpath}js/scribbler.js"></script>
