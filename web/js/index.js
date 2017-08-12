var tags, query, answer;
var needInfo = false;

function showAddedInfo() {
    $("#answer").html("");
    $('.added-info').slideDown(300);
    needInfo = true;
}

function refreshPage() {
    $('#question').val('');
    $('#answer').text('');
    $('#confirm').slideUp(300);
    $('.added-info').slideUp(300);
    needInfo = false;
}

function saveResult(confirm) {
    $.ajax({
        url: 'Answer',
        type: 'post',
        dataType: 'json',
        data: {
            action: encodeURI('saveTest'),
            question: encodeURI($('#question').val()),
            answer: encodeURI(answer),
            query: encodeURI(query),
            'tags': encodeURI(JSON.stringify(tags)),
            satisfied: encodeURI(confirm)
        },
        success: function (data) {
            console.log(data);
            $('#thanks').slideDown(300);
        },
        error: function (ts) {
            alert("Có lỗi trong việc lưu kết quả");
            console.log(ts.responseText);
        }
    });

    refreshPage();
}

function showAnswer(res) {
    var html = '';
    $('.added-info').slideUp(300);

    if (res.answer.indexOf('_') !== -1) {
        var answers = res.answer.split('_');
        html = 'Trả lời: <ul>';

        for (var i = 0; i < answers.length; i++) {
            var ans = answers[i];
            html += `<li>${ans}</li>`;
        }

        html += '</ul>';

        html += 'Tham chiếu: <ul>';
        var bases = res.base.split('_');
        for (var i = 0; i < bases.length; i++) {
            var base = bases[i];
            html += `<li>${base}</li>`;
        }
        html += '</ul>';
    } else {
        html = `Trả lời: ${res.answer} <br>Tham chiếu: ${res.base}`;
    }

    $('#answer').html(html);
    // $('#btnSubmit').val('Tiếp tục');
    $('#confirm').slideDown(300);
}

$(document).ready(function () {
    $("#btnSubmit").click(function () {
        if ($('#question').prop('disabled')) {
            refreshPage();
            return;
        }

        // $('#question').prop('disabled', true);

        if ($('#question').val() === '') {
            alert('Bạn chưa nhập câu hỏi!');
            $('#question').prop('disabled', false);
            return;
        }
        $('#thanks').slideUp(300);

        $("#answer").html("Đang lấy câu trả lời...");
        //        console.log($("#question").val());

        var dat = {
            action: needInfo ? encodeURI('reGetAnswer') : encodeURI('getAnswer'),
            question: $('#question').val()
        };

        if (needInfo) {
            dat.tags = encodeURI(JSON.stringify(tags));
            needInfo = false;
        }

        $.ajax({
            url: "Answer",
            type: 'post',
            dataType: 'json',
            data: dat,
            success: function (res) {
                console.log(res);
                tags = res.tags;
                answer = res.answer;
                query = res.query;

                if (res.has_answer === true) {
                    showAnswer(res);
                } else if (res.error === 1) {
                    alert('Hãy hỏi những câu liên quan tới giao thông!');
                    refreshPage();
                } else
                    showAddedInfo();
            },
            error: function (ts) {
                alert("Có lỗi đã xảy ra! Hãy thử lại!");
                $("#answer").html("Có lỗi đã xảy ra! Hãy thử lại!");
                $('#btnSubmit').text('Tìm kiếm');
                $('#question').prop('disabled', false);
                console.log(ts.responseText);
            }
        });
    });

    $('#refresh').click(function () {
        refreshPage();
    });

    $('#btn-yes').click(function () {
        saveResult('yes');
    });

    $('#btn-ok').click(function () {
        saveResult('ok');
    });

    $('#btn-no').click(function () {
        saveResult('no');
    });
});