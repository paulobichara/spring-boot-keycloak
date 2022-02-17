<#import "/spring.ftl" as spring>
<html>
<h2>Hello $amp{principal.getName()}</h2>
<ul>
<#list users as user>
    <li>$amp{user}</li>
</#list>
</ul>
<p>
    <a href="/logout">Logout</a>
</p>
</html>