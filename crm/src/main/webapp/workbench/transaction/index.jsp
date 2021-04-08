<%@ page import="java.util.Map" %>
<%@ page import="java.util.Set" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
    String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() + "/";

    Map<String,String> pMap = (Map<String,String>)application.getAttribute("pMap");

    Set<String> set = pMap.keySet();

%>
<!DOCTYPE html>
<html>
<head>
    <base href="<%=basePath%>">
    <meta charset="UTF-8">
    <link href="jquery/bootstrap_3.3.0/css/bootstrap.min.css" type="text/css" rel="stylesheet"/>
    <link href="jquery/bootstrap-datetimepicker-master/css/bootstrap-datetimepicker.min.css" type="text/css"
          rel="stylesheet"/>

    <script type="text/javascript" src="jquery/jquery-1.11.1-min.js"></script>
    <script type="text/javascript" src="jquery/bootstrap_3.3.0/js/bootstrap.min.js"></script>
    <script type="text/javascript" src="jquery/bootstrap-datetimepicker-master/js/bootstrap-datetimepicker.js"></script>
    <script type="text/javascript"
            src="jquery/bootstrap-datetimepicker-master/locale/bootstrap-datetimepicker.zh-CN.js"></script>

    <link rel="stylesheet" type="text/css" href="jquery/bs_pagination/jquery.bs_pagination.min.css">
    <script type="text/javascript" src="jquery/bs_pagination/jquery.bs_pagination.min.js"></script>
    <script type="text/javascript" src="jquery/bs_pagination/en.js"></script>

    <script type="text/javascript">

        var json = {

            <%

                for(String key:set){

                    String value = pMap.get(key);
            %>

            "<%=key%>" : <%=value%>,

            <%
                }

            %>

        };

        $(function () {



            //页面加载完毕后触发一个方法
            //默认展开列表的第一页，每页展现两条记录
            pageList(1, 2);

            //为查询按钮绑定事件，触发pageList方法
            $("#searchBtn").click(function () {

                $("#hidden-owner").val($.trim($("#search-owner").val()));
                $("#hidden-name").val($.trim($("#search-name").val()));
                $("#hidden-customerName").val($.trim($("#search-customerName").val()));
                $("#hidden-stage").val($.trim($("#search-stage").val()));
                $("#hidden-type").val($.trim($("#search-type").val()));
                $("#hidden-source").val($.trim($("#search-source").val()));
                $("#hidden-contactsName").val($.trim($("#search-contactsName").val()));

                pageList(1, 2);

            })

            $("#qx").click(function () {

                $("input[name=xz]").prop("checked",this.checked);

            })

            $("#tranBody").on("click",$("input[name=xz]"),function () {

                $("#qx").prop("checked",$("input[name=xz]").length==$("input[name=xz]:checked").length);

            })


            //为删除按钮绑定事件，执行交易删除操作
            $("#deleteBtn").click(function () {

                //找到复选框中所有挑√的复选框的jquery对象
                var $xz = $("input[name=xz]:checked");

                if($xz.length==0){

                    alert("请选择需要删除的记录");

                    //肯定选了，而且有可能是1条，有可能是多条
                }else{


                    if(confirm("确定删除所选中的记录吗？")){

                        //拼接参数
                        var param = "";

                        //将$xz中的每一个dom对象遍历出来，取其value值，就相当于取得了需要删除的记录的id
                        for(var i=0;i<$xz.length;i++){

                            param += "id="+$($xz[i]).val();

                            //如果不是最后一个元素，需要在后面追加一个&符
                            if(i<$xz.length-1){

                                param += "&";

                            }

                        }

                        //alert(param);
                        $.ajax({

                            url : "workbench/transaction/delete.do",
                            data : param,
                            type : "post",
                            dataType : "json",
                            success : function (data) {

                                /*

                                    data
                                        {"success":true/false}

                                 */
                                if(data.success){

                                    //删除成功后
                                    //回到第一页，维持每页展现的记录数
                                    pageList(1,$("#tranPage").bs_pagination('getOption', 'rowsPerPage'));


                                }else{

                                    alert("删除市场活动失败");

                                }


                            }

                        })


                    }




                }


            })


            //为修改按钮绑定事件，打开修改操作的模态窗口
            $("#editBtn").click(function () {

                var $xz = $("input[name=xz]:checked");

                if($xz.length==0){

                    alert("请选择需要修改的记录");

                }else if($xz.length>1){

                    alert("只能选择一条记录进行修改");

                    //肯定只选了一条
                }else{

                    var id = $xz.val();

                    $.ajax({

                        url : "workbench/transaction/getUserListAndTran.do",
                        data : {

                            "id" : id

                        },
                        type : "get",
                        dataType : "json",
                        success : function (data) {

                            //处理所有者下拉框
                            var html = "<option></option>";

                            $.each(data.uList,function (i,n) {

                                html += "<option value='"+n.id+"'>"+n.name+"</option>";

                            })

                            $("#edit-owner").html(html);

                            $("#edit-id").val(data.t.id);
                            //alert(data.t.owner);
                            $("#edit-owner").val(data.t.owner);

                            $("#edit-money").val(data.t.money);
                            $("#edit-name").val(data.t.name);
                            $("#edit-expectedDate").val(data.t.expectedDate);

                            $("#edit-customerId").val(data.t.customerId);

                            $("#edit-stage").val(data.t.stage);
                            $("#edit-type").val(data.t.type);
                            $("#edit-source").val(data.t.source);
                            $("#edit-activityName").val(data.t.activityId);
                            $("#edit-activityId").val(data.t.activityId);
                            $("#edit-contactsName").val(data.t.contactsId);
                            $("#edit-contactsId").val(data.t.contactsId);
                            $("#edit-description").val(data.t.description);
                            $("#edit-contactSummary").val(data.t.contactSummary);
                            $("#edit-nextContactTime").val(data.t.nextContactTime);

                            //所有的值都填写好之后，打开修改操作的模态窗口
                            $("#editTranModal").modal("show");

                        }

                    })

                }

            })

            //为更新按钮绑定事件，执行交易的修改操作
            $("#updateBtn").click(function () {

                $.ajax({

                    url : "workbench/transaction/update.do",
                    data : {

                        "id" : $.trim($("#edit-id").val()),
                        "owner" : $.trim($("#edit-owner").val()),
                        "money" : $.trim($("#edit-money").val()),
                        "name" : $.trim($("#edit-name").val()),
                        "expectedDate" : $.trim($("#edit-expectedDate").val()),
                        "customerId" : $.trim($("#edit-customerId").val()),
                        "stage" : $.trim($("#edit-stage").val()),
                        "type" : $.trim($("#edit-type").val()),
                        "source" : $.trim($("#edit-source").val()),
                        "activityId" : $.trim($("#edit-activityId").val()),
                        "contactsId" : $.trim($("#edit-contactsId").val()),
                        "description" : $.trim($("#edit-description").val()),
                        "contactSummary" : $.trim($("#edit-contactSummary").val()),
                        "nextContactTime" : $.trim($("#edit-nextContactTime").val())

                    },
                    type : "post",
                    dataType : "json",
                    success : function (data) {

                        if(data.success){

                            pageList($("#tranPage").bs_pagination('getOption', 'currentPage')
                                ,$("#tranPage").bs_pagination('getOption', 'rowsPerPage'));

                            //关闭修改操作的模态窗口
                            $("#editTranModal").modal("hide");

                        }else{

                            alert("修改市场活动失败");

                        }

                    }

                })

            })

            $("#edit-stage").change(function () {

                //取得选中的阶段
                var stage = $("#edit-stage").val();

                var possibility = json[stage];

                //为可能性的文本框赋值
                $("#edit-possibility").val(possibility);


            })

            //为"放大镜"图标，绑定事件，打开搜索市场活动的模态窗口
            $("#openSearchModalBtn").click(function () {

                $("#searchActivityModal").modal("show");

            })
            //为"放大镜"图标，绑定事件，打开搜索市场活动的模态窗口
            $("#openSearchModalBtn2").click(function () {

                $("#searchContactsModal").modal("show");

            })

            //为搜索操作模态窗口的 搜索框绑定事件，执行搜索并展现市场活动列表的操作
            $("#aname").keydown(function (event) {

                if(event.keyCode==13){

                    $.ajax({

                        url : "workbench/clue/getActivityListByName.do",
                        data : {

                            "aname" : $.trim($("#aname").val())

                        },
                        type : "get",
                        dataType : "json",
                        success : function (data) {

                            /*

                                data
                                    [{市场活动1},{2},{3}]

                             */

                            var html = "";

                            $.each(data,function (i,n) {

                                html += '<tr>';
                                html += '<td><input type="radio" name="xz" value="'+n.id+'"/></td>';
                                html += '<td id="'+n.id+'">'+n.name+'</td>';
                                html += '<td>'+n.startDate+'</td>';
                                html += '<td>'+n.endDate+'</td>';
                                html += '<td>'+n.owner+'</td>';
                                html += '</tr>';

                            })

                            $("#activitySearchBody").html(html);


                        }

                    })



                    return false;

                }

            })


            $("#aname2").keydown(function (event) {

                if(event.keyCode==13){

                    $.ajax({

                        url : "workbench/contacts/getContactsListByName.do",
                        data : {

                            "aname2" : $.trim($("#aname2").val())

                        },
                        type : "get",
                        dataType : "json",
                        success : function (data) {

                            var html = "";

                            $.each(data,function (i,n) {

                                html += '<tr>';
                                html += '<td><input type="radio" name="xz" value="'+n.id+'"/></td>';
                                html += '<td id="'+n.id+'">'+n.fullname+'</td>';
                                html += '<td>'+n.email+'</td>';
                                html += '<td>'+n.mphone+'</td>';
                                html += '</tr>';

                            })

                            $("#contactsSearchBody").html(html);


                        }

                    })



                    return false;

                }

            })

            //为 提交（市场活动）按钮绑定事件，填充市场活动源（填写两项信息 名字+id）
            $("#submitActivityBtn").click(function () {

                //取得选中市场活动的id
                var $xz = $("input[name=xz]:checked");
                var id = $xz.val();

                //取得选中市场活动的名字
                var name = $("#"+id).html();

                //将以上两项信息填写到 交易表单的市场活动源中
                $("#edit-activityId").val(id);
                $("#edit-activityName").val(name);

                //将模态窗口关闭掉
                $("#searchActivityModal").modal("hide");

            })

            $("#submitContactsBtn").click(function () {

                //取得选中市场活动的id
                var $xz = $("input[name=xz]:checked");
                var id = $xz.val();

                //取得选中市场活动的名字
                var name = $("#"+id).html();

                //将以上两项信息填写到 交易表单的市场活动源中
                $("#edit-contactsId").val(id);
                $("#edit-contactsName").val(name);

                //将模态窗口关闭掉
                $("#searchContactsModal").modal("hide");

            })
        });

        function pageList(pageNo, pageSize) {

            //将全选的复选框的√干掉
            $("#qx").prop("checked", false);

            //查询前，将隐藏域中保存的信息取出来，重新赋予到搜索框中
            $("#search-owner").val($.trim($("#hidden-owner").val()));
            $("#search-name").val($.trim($("#hidden-name").val()));
            $("#search-customerName").val($.trim($("#hidden-customerName").val()));
            $("#search-stage").val($.trim($("#hidden-stage").val()));
            $("#search-type").val($.trim($("#hidden-type").val()));
            $("#search-source").val($.trim($("#hidden-source").val()));
            $("#search-contactsName").val($.trim($("#hidden-contactsName").val()));

            $.ajax({

                url: "workbench/transaction/pageList.do",
                data: {

                    "pageNo": pageNo,
                    "pageSize": pageSize,

                    "owner" : $.trim($("#search-owner").val()),
                    "name" : $.trim($("#search-name").val()),
                    "customerId" : $.trim($("#search-customerName").val()),
                    "stage" : $.trim($("#search-stage").val()),
                    "type" : $.trim($("#search-type").val()),
                    "source" : $.trim($("#search-source").val()),
                    "contactsId" : $.trim($("#search-contactsName").val())


                },
                type: "get",
                dataType: "json",
                success: function (data) {

                    var html = "";

                    //每一个n就是每一个市场活动对象
                    $.each(data.dataList, function (i, n) {

                        html += '<tr class="active">';
                        html += '<td><input type="checkbox" name="xz" value="' + n.id + '"/></td>';
                        html += '<td><a style="text-decoration: none; cursor: pointer;" onclick="window.location.href=\'workbench/transaction/detail.do?id=' + n.id + '\';">' + n.name + '</a></td>';
                        html += '<td>' + n.customerId + '</td>';
                        html += '<td>' + n.stage + '</td>';
                        html += '<td>' + n.type + '</td>';
                        html += '<td>' + n.owner + '</td>';
                        html += '<td>' + n.source + '</td>';
                        html += '<td>' + n.contactsId + '</td>';
                        html += '</tr>';

                    })

                    $("#tranBody").html(html);

                    //计算总页数
                    var totalPages = data.total % pageSize == 0 ? data.total / pageSize : parseInt(data.total / pageSize) + 1;

                    //数据处理完毕后，结合分页查询，对前端展现分页信息
                    $("#tranPage").bs_pagination({
                        currentPage: pageNo, // 页码
                        rowsPerPage: pageSize, // 每页显示的记录条数
                        maxRowsPerPage: 20, // 每页最多显示的记录条数
                        totalPages: totalPages, // 总页数
                        totalRows: data.total, // 总记录条数

                        visiblePageLinks: 3, // 显示几个卡片

                        showGoToPage: true,
                        showRowsPerPage: true,
                        showRowsInfo: true,
                        showRowsDefaultInfo: true,

                        //该回调函数时在，点击分页组件的时候触发的
                        onChangePage: function (event, data) {
                            pageList(data.currentPage, data.rowsPerPage);
                        }
                    });


                }

            })

        }

    </script>
</head>
<body>

    <input type="hidden" id="hidden-owner"/>
    <input type="hidden" id="hidden-name"/>
    <input type="hidden" id="hidden-customerName"/>
    <input type="hidden" id="hidden-stage"/>
    <input type="hidden" id="hidden-type"/>
    <input type="hidden" id="hidden-source"/>
    <input type="hidden" id="hidden-contactsName"/>
    <!-- 修改交易的模态窗口 -->
    <div class="modal fade" id="editTranModal" role="dialog">
        <div class="modal-dialog" role="document" style="width: 90%;">
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal">
                        <span aria-hidden="true">×</span>
                    </button>
                    <h4 class="modal-title">修改交易</h4>
                </div>
                <div class="modal-body">
                    <form class="form-horizontal" role="form">
                        <input type="hidden" id="edit-id"/>
                        <div class="form-group">
                            <label for="edit-owner" class="col-sm-2 control-label">所有者<span style="font-size: 15px; color: red;">*</span></label>
                            <div class="col-sm-10" style="width: 300px;">
                                <select class="form-control" id="edit-owner" name="owner">
                                    <%--<option selected>zhangsan</option>
                                    <option>lisi</option>
                                    <option>wangwu</option>--%>
                                </select>
                            </div>
                            <label for="edit-money" class="col-sm-2 control-label">金额</label>
                            <div class="col-sm-10" style="width: 300px;">
                                <input type="text" class="form-control" id="edit-money" name="money">
                            </div>
                        </div>

                        <div class="form-group">
                            <label for="edit-name" class="col-sm-2 control-label">名称<span style="font-size: 15px; color: red;">*</span></label>
                            <div class="col-sm-10" style="width: 300px;">
                                <input type="text" class="form-control" id="edit-name" name="name">
                            </div>
                            <label for="edit-expectedDate" class="col-sm-2 control-label">预计成交日期<span style="font-size: 15px; color: red;">*</span></label>
                            <div class="col-sm-10" style="width: 300px;">
                                <input type="text" class="form-control time1" id="edit-expectedDate" name="expectedDate">
                            </div>
                        </div>

                        <div class="form-group">
                            <label for="edit-customerId" class="col-sm-2 control-label">客户名称<span style="font-size: 15px; color: red;">*</span></label>
                            <div class="col-sm-10" style="width: 300px;">
                                <input type="text" class="form-control" id="edit-customerId" name="customerId" placeholder="支持自动补全，输入客户不存在则新建">
                            </div>
                            <label for="edit-stage" class="col-sm-2 control-label">阶段<span style="font-size: 15px; color: red;">*</span></label>
                            <div class="col-sm-10" style="width: 300px;">
                                <select class="form-control" id="edit-stage" name="stage">
                                    <option></option>
                                    <c:forEach items="${stageList}" var="s">
                                        <option value="${s.value}">${s.text}</option>
                                    </c:forEach>
                                </select>
                            </div>
                        </div>

                        <div class="form-group">
                            <label for="edit-type" class="col-sm-2 control-label">类型</label>
                            <div class="col-sm-10" style="width: 300px;">
                                <select class="form-control" id="edit-type" name="type">
                                    <option></option>
                                    <c:forEach items="${transactionTypeList}" var="t">
                                        <option value="${t.value}">${t.text}</option>
                                    </c:forEach>
                                </select>
                            </div>
                            <label for="edit-possibility" class="col-sm-2 control-label">可能性</label>
                            <div class="col-sm-10" style="width: 300px;">
                                <input type="text" class="form-control" id="edit-possibility">
                            </div>
                        </div>

                        <div class="form-group">
                            <label for="edit-source" class="col-sm-2 control-label">来源</label>
                            <div class="col-sm-10" style="width: 300px;">
                                <select class="form-control" id="edit-source" name="source">
                                    <option></option>
                                    <c:forEach items="${sourceList}" var="s">
                                        <option value="${s.value}">${s.text}</option>
                                    </c:forEach>
                                </select>
                            </div>
                            <label for="edit-activityId" class="col-sm-2 control-label">市场活动源&nbsp;&nbsp;<a href="javascript:void(0);" id="openSearchModalBtn" style="text-decoration: none;"><span class="glyphicon glyphicon-search"></span></a></label>
                            <div class="col-sm-10" style="width: 300px;">
                                <input type="text" class="form-control" id="edit-activityName" placeholder="点击上面搜索" readonly>
                                <input type="hidden" id="edit-activityId" name="activityId"/>
                            </div>
                        </div>

                        <div class="form-group">
                            <label for="edit-contactsId" class="col-sm-2 control-label">联系人名称&nbsp;&nbsp;<a href="javascript:void(0);" id="openSearchModalBtn2" style="text-decoration: none;"><span class="glyphicon glyphicon-search"></span></a></label>
                            <div class="col-sm-10" style="width: 300px;">
                                <input type="text" class="form-control" id="edit-contactsName" placeholder="点击上面搜索" readonly>
                                <input type="hidden" id="edit-contactsId" name="contactsId"/>
                            </div>
                        </div>

                        <div class="form-group">
                            <label for="edit-description" class="col-sm-2 control-label">描述</label>
                            <div class="col-sm-10" style="width: 70%;">
                                <textarea class="form-control" rows="3" id="edit-description" name="description"></textarea>
                            </div>
                        </div>

                        <div class="form-group">
                            <label for="edit-contactSummary" class="col-sm-2 control-label">联系纪要</label>
                            <div class="col-sm-10" style="width: 70%;">
                                <textarea class="form-control" rows="3" id="edit-contactSummary" name="contactSummary"></textarea>
                            </div>
                        </div>

                        <div class="form-group">
                            <label for="edit-nextContactTime" class="col-sm-2 control-label">下次联系时间</label>
                            <div class="col-sm-10" style="width: 300px;">
                                <input type="text" class="form-control time2" id="edit-nextContactTime" name="nextContactTime">
                            </div>
                        </div>

                    </form>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
                    <button type="button" class="btn btn-primary" data-dismiss="modal" id="updateBtn">更新</button>
                </div>
            </div>
        </div>
    </div>

    <!-- 搜索市场活动的模态窗口 -->
    <div class="modal fade" id="searchActivityModal" role="dialog" >
        <div class="modal-dialog" role="document" style="width: 90%;">
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal">
                        <span aria-hidden="true">×</span>
                    </button>
                    <h4 class="modal-title">搜索市场活动</h4>
                </div>
                <div class="modal-body">
                    <div class="btn-group" style="position: relative; top: 18%; left: 8px;">
                        <form class="form-inline" role="form">
                            <div class="form-group has-feedback">
                                <input type="text" id="aname" class="form-control" style="width: 300px;" placeholder="请输入市场活动名称">
                                <span class="glyphicon glyphicon-search form-control-feedback"></span>
                            </div>
                        </form>
                    </div>
                    <table id="activityTable2" class="table table-hover" style="width: 900px; position: relative;top: 10px;">
                        <thead>
                        <tr style="color: #B3B3B3;">
                            <td></td>
                            <td>名称</td>
                            <td>开始日期</td>
                            <td>结束日期</td>
                            <td>所有者</td>
                            <td></td>
                        </tr>
                        </thead>
                        <tbody id="activitySearchBody">
                        <%--<tr>
                            <td><input type="radio" name="activity"/></td>
                            <td>发传单</td>
                            <td>2020-10-10</td>
                            <td>2020-10-20</td>
                            <td>zhangsan</td>
                        </tr>
                        <tr>
                            <td><input type="radio" name="activity"/></td>
                            <td>发传单</td>
                            <td>2020-10-10</td>
                            <td>2020-10-20</td>
                            <td>zhangsan</td>
                        </tr>--%>
                        </tbody>
                    </table>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-default" data-dismiss="modal">取消</button>
                    <button type="button" class="btn btn-primary" id="submitActivityBtn">提交</button>
                </div>
            </div>
        </div>
    </div>

    <!-- 查找联系人 -->
    <div class="modal fade" id="searchContactsModal" role="dialog">
        <div class="modal-dialog" role="document" style="width: 80%;">
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal">
                        <span aria-hidden="true">×</span>
                    </button>
                    <h4 class="modal-title">查找联系人</h4>
                </div>
                <div class="modal-body">
                    <div class="btn-group" style="position: relative; top: 18%; left: 8px;">
                        <form class="form-inline" role="form">
                            <div class="form-group has-feedback">
                                <input type="text" id="aname2" class="form-control" style="width: 300px;" placeholder="请输入市场活动名称">
                                <span class="glyphicon glyphicon-search form-control-feedback"></span>
                            </div>
                        </form>
                    </div>
                    <table id="activityTable" class="table table-hover" style="width: 900px; position: relative;top: 10px;">
                        <thead>
                        <tr style="color: #B3B3B3;">
                            <td></td>
                            <td>名称</td>
                            <td>邮箱</td>
                            <td>手机</td>
                        </tr>
                        </thead>
                        <tbody id="contactsSearchBody">
                        <%--<tr>
                            <td><input type="radio" name="activity"/></td>
                            <td>李四</td>
                            <td>lisi@bjpowernode.com</td>
                            <td>12345678901</td>
                        </tr>
                        <tr>
                            <td><input type="radio" name="activity"/></td>
                            <td>李四</td>
                            <td>lisi@bjpowernode.com</td>
                            <td>12345678901</td>
                        </tr>--%>
                        </tbody>
                    </table>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-default" data-dismiss="modal">取消</button>
                    <button type="button" class="btn btn-primary" id="submitContactsBtn">提交</button>
                </div>
            </div>
        </div>
    </div>


    <div>
    <div style="position: relative; left: 10px; top: -10px;">
        <div class="page-header">
            <h3>交易列表</h3>
        </div>
    </div>
</div>

<div style="position: relative; top: -20px; left: 0px; width: 100%; height: 100%;">

    <div style="width: 100%; position: absolute;top: 5px; left: 10px;">

        <div class="btn-toolbar" role="toolbar" style="height: 80px;">
            <form class="form-inline" role="form" style="position: relative;top: 8%; left: 5px;">

                <div class="form-group">
                    <div class="input-group">
                        <div class="input-group-addon">所有者</div>
                        <input class="form-control" type="text" id="search-owner">
                    </div>
                </div>

                <div class="form-group">
                    <div class="input-group">
                        <div class="input-group-addon">名称</div>
                        <input class="form-control" type="text" id="search-name">
                    </div>
                </div>

                <div class="form-group">
                    <div class="input-group">
                        <div class="input-group-addon">客户名称</div>
                        <input class="form-control" type="text" id="search-customerName">
				    </div>
				 </div>

				  <br>
				  <div class=" form-group">
                        <div class="input-group">
                            <div class="input-group-addon">阶段</div>
                            <select class="form-control" id="search-stage">
                                <option></option>
                                <option>资质审查</option>
                                <option>需求分析</option>
                                <option>价值建议</option>
                                <option>确定决策者</option>
                                <option>提案/报价</option>
                                <option>谈判/复审</option>
                                <option>成交</option>
                                <option>丢失的线索</option>
                                <option>因竞争丢失关闭</option>
                            </select>
                        </div>
                    </div>

                    <div class="form-group">
                        <div class="input-group">
                            <div class="input-group-addon">类型</div>
                            <select class="form-control" id="search-type">
                                <option></option>
                                <option>已有业务</option>
                                <option>新业务</option>
                            </select>
                        </div>
                    </div>

                    <div class="form-group">
                        <div class="input-group">
                            <div class="input-group-addon">来源</div>
                            <select class="form-control" id="search-source">
                                <option></option>
                                <option>广告</option>
                                <option>推销电话</option>
                                <option>员工介绍</option>
                                <option>外部介绍</option>
                                <option>在线商场</option>
                                <option>合作伙伴</option>
                                <option>公开媒介</option>
                                <option>销售邮件</option>
                                <option>合作伙伴研讨会</option>
                                <option>内部研讨会</option>
                                <option>交易会</option>
                                <option>web下载</option>
                                <option>web调研</option>
                                <option>聊天</option>
                            </select>
                        </div>
                    </div>

                    <div class="form-group">
                        <div class="input-group">
                            <div class="input-group-addon">联系人名称</div>
                            <input class="form-control" type="text" id="search-contactsName">
                        </div>
                    </div>

                    <button type="button" class="btn btn-default" id="searchBtn">查询</button>

            </form>
        </div>
        <div class="btn-toolbar" role="toolbar"
             style="background-color: #F7F7F7; height: 50px; position: relative;top: 10px;">
            <div class="btn-group" style="position: relative; top: 18%;">
                <button type="button" class="btn btn-primary" onclick="window.location.href='workbench/transaction/add.do';"><span class="glyphicon glyphicon-plus"></span> 创建</button>
                <button type="button" class="btn btn-default" id="editBtn"><span class="glyphicon glyphicon-pencil"></span> 修改</button><%--onclick="window.location.href='edit.html';" --%>
                <button type="button" class="btn btn-danger" id="deleteBtn"><span class="glyphicon glyphicon-minus"></span> 删除</button>
            </div>


        </div>
        <div style="position: relative;top: 10px;">
            <table class="table table-hover">
                <thead >
                <tr style="color: #B3B3B3;">
                    <td><input type="checkbox" id="qx"/></td>
                    <td>名称</td>
                    <td>客户名称</td>
                    <td>阶段</td>
                    <td>类型</td>
                    <td>所有者</td>
                    <td>来源</td>
                    <td>联系人名称</td>
                </tr>
                </thead>
                <tbody id="tranBody">
                <tr>
                    <td><input type="checkbox"/></td>
                    <td><a style="text-decoration: none; cursor: pointer;"
                           onclick="window.location.href='workbench/transaction/detail.do?id=df952ef2ea50402db2e36247166fc90f';">交易</a>
                    </td>
                    <td>动力节点</td>
                    <td>谈判/复审</td>
                    <td>新业务</td>
                    <td>zhangsan</td>
                    <td>广告</td>
                    <td>李四</td>
                </tr>
                <tr class="active">
                    <%--<td><input type="checkbox"/></td>
                    <td><a style="text-decoration: none; cursor: pointer;"
                           >动力节点-交易01</a></td>&lt;%&ndash;onclick="window.location.href='';"&ndash;%&gt;
                    <td>动力节点</td>
                    <td>谈判/复审</td>
                    <td>新业务</td>
                    <td>zhangsan</td>
                    <td>广告</td>
                    <td>李四</td>--%>
                </tr>
                </tbody>
            </table>
        </div>

        <div style="height: 50px; position: relative;top: 30px;">

            <div id="tranPage"></div>

        </div>

    </div>

</div>
</body>
</html>