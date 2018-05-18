/* tabContainer */
$(function () {
    $(".tab_content").hide();
    $(".tab_content:first").show();
    $(".tabs_nav li a").click(function () {
        $(".tab_content").hide()
        var activeTab = $(this).attr("rel");
        $("#" + activeTab).show()
    });

	tab('#tab',0);

	var name = getParameters("name"); // 이름
	var age = getParameters("age"); // 나이

	var sex = getParameters("sex"); // 성별
	var height = getParameters("height"); // 키

	var weight = getParameters("weight"); // 체중
	var strweightIndex = getParameters("strweightIndex"); // 체중그래프
	var excerGoalWeight = getParameters("excerGoalWeight"); // 운동목표
	var adjGoalWeight = getParameters("adjGoalWeight"); // 조절목표
	var minWeight = getParameters("minWeight"); // 체중 하한
	var maxWeight = getParameters("maxWeight"); // 체중 상한

	var bodyFatPer = getParameters("bodyFatPer"); // 체지방률
	var strgraphbodyFatPervalue = getParameters("strgraphbodyFatPervalue"); // 체지방률 그래프
    var excerGoalBodyFat = getParameters("excerGoalBodyFat"); // 운동목표
    var adjGoalBodyFat = getParameters("adjGoalBodyFat"); // 조절목표
    var minBodyFat = getParameters("minBodyFat"); // 체지방률 하한
    var maxBodyFat = getParameters("maxBodyFat"); // 체지방률 상한

	var musMass = getParameters("musMass"); // 근육량
	var strgraphmuscleIndex = getParameters("strgraphmuscleIndex"); // 근육량 그래프
    var excerGoalMusMass = getParameters("excerGoalMusMass"); // 운동목표
    var adjGoalMusMass = getParameters("adjGoalMusMass"); // 조절목표
    var minMusMass = getParameters("minMusMass"); // 근육량 하한
    var maxMusMass = getParameters("maxMusMass"); // 근육량 상한

	var bmi = getParameters("bmi"); // BMI
	var strfBMI = getParameters("strfBMI"); // BMI
	var strgrapfBMI = getParameters("strgrapfBMI"); // BMI
    var excerGoalBmi = getParameters("excerGoalBmi"); // 운동목표
    var adjGoalBmi = getParameters("adjGoalBmi"); // 조절목표
    var minBmi = getParameters("minBmi"); // BMI 하한
    var maxBmi = getParameters("maxBmi"); // BMI 상한

	var oneKcal = getParameters("oneKcal"); // 1일 권장 칼로리
    var basicMeta = getParameters("basicMeta"); // 기초대사량
    var digeMeta = getParameters("digeMeta"); // 소화대사량
    var activiMeta = getParameters("activiMeta"); // 활동대사량

    var strGraphBasicMeta = getParameters("strGraphBasicMeta"); // 기초대사량
    var strGraphDigestMeta = getParameters("strGraphDigestMeta"); // 소화대사량
    var strGraphActiviyMeta = getParameters("strGraphActiviyMeta"); // 활동대사량

    var bodyWater = getParameters("bodyWater"); // 체수분
    var minBodyWater = getParameters("minBodyWater"); // 체수분 하한
    var maxBodyWater = getParameters("maxBodyWater"); // 체수분 상한
    var bodyWaterEval = getParameters("bodyWaterEval"); // 체수분 평가

    var protein = getParameters("protein"); // 단백질
    var minProtein = getParameters("minProtein"); // 단백질 하한
    var maxProtein = getParameters("maxProtein"); // 단백질 상한
    var proteinEval = getParameters("proteinEval"); // 단백질 평가

    var minerals = getParameters("minerals"); // 무기질
    var minMinerals = getParameters("minMinerals"); // 무기질 하한
    var maxMinerals = getParameters("maxMinerals"); // 무기질 상한
    var mineralsEval = getParameters("mineralsEval"); // 무기질 평가

    var bodyFatPer2 = getParameters("bodyFatPer2"); // 체지방
    var minBodyFatPer2 = getParameters("minBodyFatPer2"); // 체지방 하한
    var maxBodyFatPer2 = getParameters("maxBodyFatPer2"); // 체지방 상한
    var bodyFatPer2Eval = getParameters("bodyFatPer2Eval"); // 체지방 평가

	document.getElementById("name").innerHTML = name;
	document.getElementById("age").innerHTML = age;
	document.getElementById("sex").innerHTML = sex;
	document.getElementById("height").innerHTML = height + "cm";

	document.getElementById("oneKcal").innerHTML = oneKcal;
	document.getElementById("kcal1").innerHTML = basicMeta + " kcal";
	document.getElementById("kcal2").innerHTML = digeMeta + " kcal";
	document.getElementById("kcal3").innerHTML = activiMeta + " kcal";

	document.getElementById("weight").innerHTML = weight + "kg";
	document.getElementById("bodyFatPer").innerHTML = bodyFatPer + "%(" +bodyFatPer2 + "kg)" ;
	//document.getElementById("musMass").innerHTML = strgraphmuscleIndex + "(" + musMass + "kg)";
	document.getElementById("musMass").innerHTML = musMass + "kg";
	document.getElementById("bmi").innerHTML = bmi + "kg/m2";

    document.getElementById("excerGoalWeight").innerHTML = excerGoalWeight +"kg";
    document.getElementById("adjGoalWeight").innerHTML = adjGoalWeight +"kg";
    document.getElementById("excerGoalBodyFat").innerHTML = excerGoalBodyFat +"kg";
    document.getElementById("adjGoalBodyFat").innerHTML = adjGoalBodyFat + "kg";
    document.getElementById("excerGoalMusMass").innerHTML = excerGoalMusMass +"kg";
    document.getElementById("adjGoalMusMass").innerHTML = adjGoalMusMass +"kg";
    document.getElementById("excerGoalBmi").innerHTML = excerGoalBmi;
    document.getElementById("adjGoalBmi").innerHTML = adjGoalBmi;

	document.getElementById("bodyWater").innerHTML = bodyWater;
	document.getElementById("protein").innerHTML = protein;
	document.getElementById("minerals").innerHTML = minerals;
	document.getElementById("bodyFatPer2").innerHTML = bodyFatPer2;

	document.getElementById("bodyWaterMinMax").innerHTML = minBodyWater + "~" + maxBodyWater;
	document.getElementById("proteinMinMax").innerHTML = minProtein + "~" + maxProtein;
	document.getElementById("mineralsMinMax").innerHTML = minMinerals + "~" + maxMinerals;
	document.getElementById("bodyFatPer2MinMax").innerHTML = minBodyFatPer2 + "~" + maxBodyFatPer2;

	document.getElementById("weight_prog").style.width = strweightIndex + "%";
	document.getElementById("bodyFatPer_prog").style.width = strgraphbodyFatPervalue + "%";
	document.getElementById("musmass_prog").style.width = strgraphmuscleIndex + "%";
	document.getElementById("BMI_prog").style.width = strgrapfBMI + "%";
	document.getElementById("base_meta_prog").style.width = strGraphBasicMeta + "%";
	document.getElementById("digest_meta_prog").style.width = strGraphDigestMeta + "%";
	document.getElementById("activity_meta_prog").style.width = strGraphActiviyMeta + "%";


    if (bodyWaterEval == 0 )
        bodyWaterEval = "표준이하"
    else if (bodyWaterEval == 1 )
        bodyWaterEval = "표준"
    else
        bodyWaterEval = "표준이상"
	document.getElementById("bodyWaterEval").innerHTML = bodyWaterEval;

    if (proteinEval == 0 )
        proteinEval = "표준이하"
    else if (proteinEval == 1 )
        proteinEval = "표준"
    else
        proteinEval = "표준이상"
	document.getElementById("proteinEval").innerHTML = proteinEval;

    if (mineralsEval == 0 )
        mineralsEval = "표준이하"
    else if (mineralsEval == 1 )
        mineralsEval = "표준"
    else
        mineralsEval = "표준이상"
	document.getElementById("mineralsEval").innerHTML = mineralsEval;

    if (bodyFatPer2Eval == 0 )
        bodyFatPer2Eval = "표준이하"
    else if (bodyFatPer2Eval == 1 )
        bodyFatPer2Eval = "표준"
    else
        bodyFatPer2Eval = "표준이상"
	document.getElementById("bodyFatPer2Eval").innerHTML = bodyFatPer2Eval;

	var myDate = new Date();
	var year = myDate.getFullYear();
	var month = myDate.getMonth() + 1;
	var date = myDate.getDate();
	var hours = myDate.getHours();
	var minutes = myDate.getMinutes();
	var seconds = myDate.getSeconds();
	var formatDate = year + "-" + month +"-" + date + " " + hours + ":" + minutes + ":" + seconds;

	document.getElementById("checkDate").innerHTML = formatDate;


});

function tab(e, num){
    var num = num || 0;
    var menu = $(e).children();
    var con = $(e+'_con').children();
    var select = $(menu).eq(num);
    var i = num;

    select.addClass('on');
    con.eq(num).show();

    menu.click(function(){
        if(select!==null){
            select.removeClass("on");
            con.eq(i).hide();
        }

        select = $(this);	
        i = $(this).index();

        select.addClass('on');
        con.eq(i).show();
    });
}

function getParameters (paramName) {
    // 리턴값을 위한 변수 선언
    var returnValue;

    // 현재 URL 가져오기
    var url = location.href;

    // get 파라미터 값을 가져올 수 있는 ? 를 기점으로 slice 한 후 split 으로 나눔
    var parameters = (url.slice(url.indexOf('?') + 1, url.length)).split('&');

    // 나누어진 값의 비교를 통해 paramName 으로 요청된 데이터의 값만 return
    for (var i = 0; i < parameters.length; i++) {
        var varName = parameters[i].split('=')[0];
        if (varName.toUpperCase() == paramName.toUpperCase()) {
            returnValue = parameters[i].split('=')[1];
            return decodeURIComponent(returnValue);
        }
    }
}