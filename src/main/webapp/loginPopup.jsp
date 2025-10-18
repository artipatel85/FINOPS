<HTML>
<script >

	var newwindow = window.open("toc.jsp", "popupWin", "resizable=yes, width=1024,height=768,menubar=no, scrollbars=yes, left=0,top=0, status=yes, location=false ");
	if (window.focus) 
	{
		newwindow.focus();
		newwindow.resizeTo(screen.availWidth, screen.availHeight);
		newwindow.moveTo(0,0)
		
		if(window!=newwindow)
		{
			self.opener = top;
			self.close();
		}
	}
	
</script>

</HTML>
