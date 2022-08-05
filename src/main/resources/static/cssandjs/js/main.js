function showOrHideGrade(gradeType) {
    if (gradeType === "math") {
        var x = document.getElementById("mathGrade");
        if (x.style.display === "none") {
            x.style.display = "block";
        } else {
            x.style.display = "none";
        }
    }
    if (gradeType === "science") {
        var x = document.getElementById("scienceGrade");
        if (x.style.display === "none") {
            x.style.display = "block";
        } else {
            x.style.display = "none";
        }
    }
    if (gradeType === "history") {
        var x = document.getElementById("historyGrade");
        if (x.style.display === "none") {
            x.style.display = "block";
        } else {
            x.style.display = "none";
        }
    }
}

function deleteStudent(id) {
    fetch("/delete/student/" + id, {
        method: 'DELETE'
    }).then(response => {
        console.log({response})
    }).then(data =>
        console.error({data})
    );
}

function deleteMathGrade(id) {
    fetch("/grades/" + id + "/" + "math", {
        method: 'DELETE'
    }).then(response => {
        console.log({response})
    }).then(data =>
        console.error({data})
    );
}

function deleteScienceGrade(id) {
    fetch("/grades/" + id + "/" + "science", {
        method: 'DELETE'
    }).then(response => {
        console.log({response})
    }).then(data =>
        console.error({data})
    );
}

function deleteHistoryGrade(id) {
    fetch("/grades/" + id + "/" + "history", {
        method: 'DELETE'
    }).then(response => {
        console.log({response})
    }).then(data =>
        console.error({data})
    );
}

function studentInfo(id) {
    window.location.href = "/studentInformation/" + id;
}