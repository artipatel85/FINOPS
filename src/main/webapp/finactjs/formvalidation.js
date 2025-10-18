/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

 $(document).ready(function(){
		
             $('#registration-form').validate({
        rules: {
            name: {
                required: true,
                minlength: 5
                
            },
            party: {
                required: true
            }
        },
        messages: {
             name: "please fill this field"
        }
//        highlight: function(element) {
//                  $(element).closest('.control-group')
//                 .removeClass('success').addClass('error');
//			},
//			success: function(element) {
//				element
//				.text('OK!').addClass('valid')
//				.closest('.control-group').removeClass('error').addClass('success');
//			}
    });
});
