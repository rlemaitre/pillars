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
<script src="https://cdnjs.cloudflare.com/ajax/libs/highlight.js/11.9.0/highlight.min.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/highlight.js/11.9.0/languages/dockerfile.min.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/highlight.js/11.9.0/languages/gradle.min.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/highlight.js/11.9.0/languages/scala.min.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/highlight.js/11.9.0/languages/sql.min.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/highlight.js/11.9.0/languages/yaml.min.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/highlight.js/11.9.0/languages/xml.min.js"></script>
<% if (config.site_plausibleUrl && config.site_domain) { %>
<script defer data-domain="${config.site_domain}" src="${config.site_plausibleUrl}"></script>
<% } %>
<script>hljs.highlightAll();</script>
<script src="${content.rootpath}js/scribbler.js"></script>
