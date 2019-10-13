<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/html">
<head>
    <meta charset="utf‐8">
    <title>Hello World!</title>
</head>
<body>
<#--取出普通字符串-->
Hello ! ${name}

<#--遍历List-->
<table>
    <tr>
        <td>序号</td>
        <td>姓名</td>
        <td>年龄</td>
        <td>钱包</td>
        <td>生日</td>
    </tr>
    <#--if指令，??判断非空，不为空则返回true-->
    <#if stus??>
        <#list stus as s>
            <tr>
                <td>${s_index+1}</td>
                <td>${s.name}</td>
                <td>${s.age}</td>
                <td>${s.money}</td>
                <td>${s.birthday?datetime}</td>
            </tr>
        </#list>
    </#if>
</table>

<#--遍历map-->
<#if stuMap??>
    <table>
        <#list stuMap?keys as key>
            <tr>
                <td>${key_index+1}</td>
                <td>${stuMap[key].name}</td>
                <td>${stuMap[key].age}</td>
            </tr>
        </#list>
    </table>
</#if>
</body>
</html>
