// ДАН-login frontend logic (vanilla JS) — inline handler-гүй, data-attribute суурьтай.
(function () {
  'use strict';

  // ── Login: нэвтрэлтийн арга сонгох таб ──
  var tabs = document.querySelectorAll('.tab');
  var methodInput = document.getElementById('method');
  // Эхэнд идэвхтэй табын аргыг далд талбарт тааруулна (эрэмбээс үл хамаарч зөв)
  var activeTab = document.querySelector('.tab.active');
  if (activeTab && methodInput) { methodInput.value = activeTab.dataset.method; }
  tabs.forEach(function (tab) {
    tab.addEventListener('click', function () {
      tabs.forEach(function (t) { t.classList.remove('active'); });
      tab.classList.add('active');
      if (methodInput) { methodInput.value = tab.dataset.method; }
    });
  });

  // ── Нууц үг харах/нуух ──
  var eye = document.querySelector('[data-toggle-pw]');
  if (eye) {
    eye.addEventListener('click', function () {
      var pw = document.getElementById('pw');
      if (pw) { pw.type = pw.type === 'password' ? 'text' : 'password'; }
    });
  }

  // ── Login form: Р/Д үсэг + дугаарыг РД болгон нийлүүлэх ──
  var form = document.getElementById('loginForm');
  if (form) {
    form.addEventListener('submit', function () {
      var l1 = (document.getElementById('rdL1').value || '').trim();
      var l2 = (document.getElementById('rdL2').value || '').trim();
      var num = (document.getElementById('rdNum').value || '').trim();
      document.getElementById('personalCode').value = (l1 + l2 + num).toUpperCase();
    });
  }

  // ── Verify: нэг удаагийн кодын төлөв шалгах (polling) ──
  var verify = document.querySelector('[data-session-id]');
  if (verify) {
    var sessionId = verify.dataset.sessionId;
    var statusEl = document.getElementById('status');
    var poll = function () {
      fetch('/auth/login/poll?sessionId=' + encodeURIComponent(sessionId))
        .then(function (r) { return r.json(); })
        .then(function (data) {
          if (data.status === 'SUCCESS') {
            if (statusEl) { statusEl.textContent = 'Амжилттай! Чиглүүлж байна…'; }
            window.location = data.redirectTo;
            return;
          }
          if (data.status === 'FAILED') {
            if (statusEl) { statusEl.textContent = 'Баталгаажуулалт амжилтгүй боллоо.'; }
            var sp = document.querySelector('.spinner');
            if (sp) { sp.style.display = 'none'; }
            return;
          }
          setTimeout(poll, 1500);
        })
        .catch(function () { setTimeout(poll, 2000); });
    };
    setTimeout(poll, 1500);
  }
})();
