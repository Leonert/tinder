<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Title</title>
</head>
<body>
<#list name>
    hello once before
    <#items as qq>
   <p> ${qq} Part repeated for each item </p>
    </#items>
    hello once after
<#else >
    You haven`t liked any users
</#list>
</body>
</html>