<%
    //let's build some urls.
    //what's the correct source file name with path?
    def sourceFileName = content?.uri?.replaceAll("[.]html", (content.file =~ /[.][^.]+$/)[0])
    def subject = java.net.URLEncoder.encode("Docs: Feedback for '${content?.title}'", "UTF-8")
%>
<div class="right__nav">
<% if (content?.body.contains('<!-- endtoc -->')) { %>
        ${content?.body?.split("(?ms)<!-- endtoc -->",2)[0]}
<% } %>
    <ul>
<% if (config.site_gitRepoUrl) { %>
        <li>
            <a href="${config.site_gitRepoUrl}/${sourceFileName}" target="_blank"><i class="fa fa-edit fa-fw"></i> Improve this doc</a>
        </li>
<% } %>
<% if (config.site_issueUrl) {%>
        <li>
            <a href="${config.site_issueUrl}?title=${subject}&body=%0A%0A%5BEnter%20feedback%20here%5D%0A%0A%0A---%0A%23page:%20${config.site_gitRepoUrl}/${sourceFileName}%0A%23branch:%20${config.site_branch}" target="_blank">
                <i class="fa fa-github"></i> Create an issue
            </a>
        </li>
<% } %>
    </ul>
</div>
