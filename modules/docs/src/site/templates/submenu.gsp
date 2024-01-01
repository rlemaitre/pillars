<%
    if (config.site_menu=="") {
        config.site_menu=[:]
    }
    def menu = content.menu[content['jbake-menu']]

    def printMenu(def c, int index, def entries) {
        String result = ''
        if(entries) {
            String htmlClass = (index == 0) ? ' ' : '';
            result = result + """
                        <ul>"""
            entries?.sort{a, b ->a.order <=> b.order ?: a.title <=> b.title }?.each { entry ->
                def hasChild = (entry.children) ? 'with-child' : 'without-child'
                def isActive = (c.uri==entry.uri) ? 'active' : ''
                result = result + """
                            <li>"""
                if (entry.uri) {
                    result = result + """
                                <a class="${isActive}" href="${c.rootpath}${entry.uri}">${entry.title?:entry}</a>"""
                } else {
                    def title = entry.title?:entry
                    if (config.site_menu[title]) {
                        title = config.site_menu[title]
                    }
                    result = result + """
                                ${title}"""
                }
                if (entry.children) {
                    result = result + printMenu(c, index + 1, entry.children)
                }
                result = result + '''
                            </li>'''
            }
            result = result + '''
                        </ul>'''
        }
        return result
    }
%>
        <form action="${content.rootpath}search.html">
            <input aria-label="Search this site…" name="q" autocomplete="off" class="" placeholder="Search this site…" type="search">
            <button aria-expanded="false" aria-label="Toggle section navigation" class="fa fa-bars" data-toggle="collapse" type="button">
            </button>
        </form>
        <%= printMenu(content, 0, menu) %>
