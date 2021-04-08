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

<link href="jquery/bootstrap_3.3.0/css/bootstrap.min.css" type="text/css" rel="stylesheet" />
<link href="jquery/bootstrap-datetimepicker-master/css/bootstrap-datetimepicker.min.css" type="text/css" rel="stylesheet" />

<script type="text/javascript" src="jquery/jquery-1.11.1-min.js"></script>
<script type="text/javascript" src="jquery/bootstrap_3.3.0/js/bootstrap.min.js"></script>
<script type="text/javascript" src="jquery/bootstrap-datetimepicker-master/js/bootstrap-datetimepicker.js"></script>
<script type="text/javascript" src="jquery/bootstrap-datetimepicker-master/locale/bootstrap-datetimepicker.zh-CN.js"></script>

	<script type="text/javascript" src="jquery/bs_typeahead/bootstrap3-typeahead.min.js"></script>


	<script>

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

		//object Object
		//alert(json);

		/*

			关于阶段和可能性
				是一种一一对应的关系
				一个阶段对应一个可能性
				我们现在可以将阶段和可能性想象成是一种键值对之间的对应关系
				以阶段为key，通过选中的阶段，触发可能性value

				stage               possibility
				key					value
				01资质审查			10
				02需求分析			25
				03价值建议			40
				...
				...
				07成交				100
				08..				0
				09..				0

				对于以上的数据，通过观察得到结论
				（1）数据量不是很大
				（2）这是一种键值对的对应关系

				如果同时满足以上两种结论，那么我们将这样的数据保存到数据库表中就没有什么意义了
				如果遇到这种情况，我们需要用到properties属性文件来进行保存
				stage2Possibility.properties
				01资质审查=10
				02需求分析=20
				....


				stage2Possibility.properties这个文件表示的是阶段和键值对之间的对应关系
				将来，我们通过stage，以及对应关系，来取得可能性这个值
				这种需求在交易模块中需要大量的使用到

				所以我们就需要将该文件解析在服务器缓存中
				application.setAttribute(stage2Possibility.properties文件内容)


		 */


		$(function () {

			$("#create-customerName").typeahead({
				source: function (query, process) {
					$.get(
							"workbench/transaction/getCustomerName.do",
							{ "name" : query },
							function (data) {
								//alert(data);

								/*

									data
										[{客户名称1},{2},{3}]

								 */

								process(data);
							},
							"json"
					);
				},
				delay: 500
			});



			$(".time1").datetimepicker({
				minView: "month",
				language:  'zh-CN',
				format: 'yyyy-mm-dd',
				autoclose: true,
				todayBtn: true,
				pickerPosition: "bottom-left"
			});

			$(".time2").datetimepicker({
				minView: "month",
				language:  'zh-CN',
				format: 'yyyy-mm-dd',
				autoclose: true,
				todayBtn: true,
				pickerPosition: "top-left"
			});

			//为阶段的下拉框，绑定选中下拉框的事件，根据选中的阶段填写可能性
			$("#create-stage").change(function () {

				//取得选中的阶段
				var stage = $("#create-stage").val();

				/*

					目标：填写可能性

					阶段有了stage
					阶段和可能性之间的对应关系pMap，但是pMap是java语言中的键值对关系（java中的map对象）
					我们首先得将pMap转换为js中的键值对关系json

					我们要做的是将pMap
						pMap.put("01资质审查",10);
						pMap.put("02需求分析",25);
						...

						转换为

						var json = {"01资质审查":10,"02需求分析":25...};

					以上我们已经将json处理好了

					接下来取可能性

				 */

				//alert(stage);

				/*

					我们现在以json.key的形式不能取得value
					因为今天的stage是一个可变的变量
					如果是这样的key，那么我们就不能以传统的json.key的形式来取值
					我们要使用的取值方式为
					json[key]


				 */
				var possibility = json[stage];
				//alert(possibility);

				//为可能性的文本框赋值
				$("#create-possibility").val(possibility);


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
				$("#activityId").val(id);
				$("#create-activitySrc").val(name);

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
				$("#contactsId").val(id);
				$("#create-contactsName").val(name);

				//将模态窗口关闭掉
				$("#searchContactsModal").modal("hide");

			})

			//为保存按钮绑定事件，执行交易添加操作
			$("#saveBtn").click(function () {

				//发出传统请求，提交表单
				$("#tranForm").submit();

			})


		})

	</script>

</head>
<body>
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
	
	
	<div style="position:  relative; left: 30px;">
		<h3>创建交易</h3>
	  	<div style="position: relative; top: -40px; left: 70%;">
			<button type="button" class="btn btn-primary" id="saveBtn">保存</button>
			<a href="javascript:history.go(-1);" class="btn btn-default" type="button">取消</a>
			<%--<input type="button" class="btn btn-default" οnclick="history.go(-1)" value="取消"/>--%>
		</div>
		<hr style="position: relative; top: -40px;">
	</div>
	<form action="workbench/transaction/save.do" id="tranForm" method="post" class="form-horizontal" role="form" style="position: relative; top: -30px;">
		<div class="form-group">
			<label for="create-transactionOwner" class="col-sm-2 control-label">所有者<span style="font-size: 15px; color: red;">*</span></label>
			<div class="col-sm-10" style="width: 300px;">
				<select class="form-control" id="create-transactionOwner" name="owner">
				  <option></option>
					<c:forEach items="${uList}" var="u">
						<option value="${u.id}" ${user.id eq u.id ? "selected" : ""}>${u.name}</option>
					</c:forEach>
				</select>
			</div>
			<label for="create-amountOfMoney" class="col-sm-2 control-label">金额</label>
			<div class="col-sm-10" style="width: 300px;">
				<input type="text" class="form-control" id="create-amountOfMoney" name="money">
			</div>
		</div>
		
		<div class="form-group">
			<label for="create-transactionName" class="col-sm-2 control-label">名称<span style="font-size: 15px; color: red;">*</span></label>
			<div class="col-sm-10" style="width: 300px;">
				<input type="text" class="form-control" id="create-transactionName" name="name">
			</div>
			<label for="create-expectedClosingDate" class="col-sm-2 control-label">预计成交日期<span style="font-size: 15px; color: red;">*</span></label>
			<div class="col-sm-10" style="width: 300px;">
				<input type="text" class="form-control time1" id="create-expectedClosingDate" name="expectedDate">
			</div>
		</div>

		<div class="form-group">
			<label for="create-customerName" class="col-sm-2 control-label">客户名称<span style="font-size: 15px; color: red;">*</span></label>

			<div class="col-sm-10" style="width: 300px;"><%--readonly--%>
				<c:if test="${empty cus.name}">
					<input type="text" class="form-control" id="create-customerName" name="customerName" placeholder="支持自动补全，输入客户不存在则新建">
					<input type="hidden" id="tag" value="0" name="tag">
				</c:if>
				<c:if test="${not empty cus.name}">
					<input type="text" class="form-control" id="create-customerName" name="customerName" placeholder="支持自动补全，输入客户不存在则新建" value="${cus.name}" readonly>
					<input type="hidden" id="tag" value="1" name="tag">
				</c:if>
			</div>
			<label for="create-stage" class="col-sm-2 control-label">阶段<span style="font-size: 15px; color: red;">*</span></label>
			<div class="col-sm-10" style="width: 300px;">
			  <select class="form-control" id="create-stage" name="stage">
			  	<option></option>
			  	<c:forEach items="${stageList}" var="s">
					<option value="${s.value}">${s.text}</option>
				</c:forEach>
			  </select>
			</div>
		</div>
		
		<div class="form-group">
			<label for="create-transactionType" class="col-sm-2 control-label">类型</label>
			<div class="col-sm-10" style="width: 300px;">
				<select class="form-control" id="create-transactionType" name="type">
				  <option></option>
					<c:forEach items="${transactionTypeList}" var="t">
						<option value="${t.value}">${t.text}</option>
					</c:forEach>
				</select>
			</div>
			<label for="create-possibility" class="col-sm-2 control-label">可能性</label>
			<div class="col-sm-10" style="width: 300px;">
				<input type="text" class="form-control" id="create-possibility">
			</div>
		</div>
		
		<div class="form-group">
			<label for="create-clueSource" class="col-sm-2 control-label">来源</label>
			<div class="col-sm-10" style="width: 300px;">
				<select class="form-control" id="create-clueSource" name="source">
				  <option></option>
					<c:forEach items="${sourceList}" var="s">
						<option value="${s.value}">${s.text}</option>
					</c:forEach>
				</select>
			</div>
			<%--
			<label for="create-activitySrc" class="col-sm-2 control-label">市场活动源&nbsp;&nbsp;<a href="javascript:void(0);" data-toggle="modal" data-target="#findMarketActivity"><span class="glyphicon glyphicon-search"></span></a></label>
			<div class="col-sm-10" style="width: 300px;">
				<input type="text" class="form-control" id="create-activitySrc" value="发传单1">
				<input type="hidden" name="activityId" value="1d598ac0910a4a2ebb262b0a27eae7cc"/>
			</div>--%>
			<label for="activityId" class="col-sm-2 control-label">市场活动源&nbsp;&nbsp;<a href="javascript:void(0);" id="openSearchModalBtn" style="text-decoration: none;"><span class="glyphicon glyphicon-search"></span></a></label>
			<div class="col-sm-10" style="width: 300px;">
				<input type="text" class="form-control" id="create-activitySrc" placeholder="点击上面搜索" readonly>
				<input type="hidden" id="activityId" name="activityId"/>
			</div>
		</div>
		
		<div class="form-group">
			<%--<label for="create-contactsName" class="col-sm-2 control-label">联系人名称&nbsp;&nbsp;<a href="javascript:void(0);" data-toggle="modal" data-target="#findContacts"><span class="glyphicon glyphicon-search"></span></a></label>
			<div class="col-sm-10" style="width: 300px;">
				<input type="text" class="form-control" id="create-contactsName" value="马云">
				<input type="hidden" name="contactsId" value="620619cd4710405aaa14c049cd071e1c"/>
			</div>--%>
			<label for="activityId" class="col-sm-2 control-label">联系人名称&nbsp;&nbsp;<a href="javascript:void(0);" id="openSearchModalBtn2" style="text-decoration: none;"><span class="glyphicon glyphicon-search"></span></a></label>
			<div class="col-sm-10" style="width: 300px;">
				<input type="text" class="form-control" id="create-contactsName" placeholder="点击上面搜索" readonly>
				<input type="hidden" id="contactsId" name="contactsId"/>
			</div>
		</div>
		
		<div class="form-group">
			<label for="create-describe" class="col-sm-2 control-label">描述</label>
			<div class="col-sm-10" style="width: 70%;">
				<textarea class="form-control" rows="3" id="create-describe" name="description"></textarea>
			</div>
		</div>
		
		<div class="form-group">
			<label for="create-contactSummary" class="col-sm-2 control-label">联系纪要</label>
			<div class="col-sm-10" style="width: 70%;">
				<textarea class="form-control" rows="3" id="create-contactSummary" name="contactSummary"></textarea>
			</div>
		</div>
		
		<div class="form-group">
			<label for="create-nextContactTime" class="col-sm-2 control-label">下次联系时间</label>
			<div class="col-sm-10" style="width: 300px;">
				<input type="text" class="form-control time2" id="create-nextContactTime" name="nextContactTime">
			</div>
		</div>
		
	</form>
</body>
</html>