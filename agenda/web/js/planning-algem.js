/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @since 09/05/15 09:33
 * @version 1.0.6
 * @returns {void}
 */

init = function() {
  $("img").hover(
    function() {
      $(this).fadeTo("fast", 0.6);
    },
    function() {
      $(this).fadeTo("fast", 1.0);
  });
var picker = $("#datepicker");
  //picker.datepicker({ appendText: "(jj-mm-yyyy)", changeMonth: true, changeYear: true })
  picker.datepicker({changeMonth: true, changeYear: true, autoSize: true});
  picker.datepicker('setDate', currentDate);
  picker.datepicker("refresh");
  $('#estabSelection').val(estabId);
  document.title = 'Agenda ' + $('#estabSelection option:selected').text();
  picker.change(function() {
    window.location = 'today.html?d=' + this.value + '&e=' + estabId;
  });
  // Next Day Link
  $('a#next').click(function() {
    var date = new Date(picker.datepicker('getDate'));
    date.setDate(date.getDate() + 1);
    picker.datepicker('setDate', date).change();
    return false;
  });
  // Previous Day Link
  $('a#previous').click(function() {
    var date = new Date(picker.datepicker('getDate'));
    date.setDate(date.getDate() - 1);
    picker.datepicker('setDate', date).change();
    return false;
  });
  $('#estabSelection').change(function() {
    var eId = $('#estabSelection option:selected').val();
    window.location = 'today.html?d=' + $("#datepicker").val() + '&e=' + eId;
  });
  $('#help').hover(function() {
    $(this).css('cursor', 'pointer');
    $('#help-content').toggle();
  });
  $('#tel').click(function() {
    $(this).css('cursor', 'pointer');
    $('#tel-content').toggle();
  });
  $('#tel-close').click(function() {
    $(this).css('cursor', 'pointer');
    $('#tel-content').hide();
  });

};