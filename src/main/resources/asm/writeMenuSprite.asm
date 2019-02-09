//function writeSprite(int a1_uAddr, int a2_screenX, int a3_screenY, int t5_clut, int t6_rgb)
//free used register:v0,v1,a0
addiu a2,a2,a   //screenX++
sh a3,fff8(t0)  //write screen y
lui v0,0400
sw v0,0(t1)
sw t6,fff2(t0)	//write sprite head

lbu v0,0(a1)	//load char encoding,calculate and write u
nop
andi v0,v0,f
sll v1,v0,3
sll a0,v0,1
add v0,v1,a0
sb v0,fffa(t0)

lbu v0,0(a1)  	//load char encoding,calculate and write v
nop
srl v0,v0,4
sll v1,v0,3
sll a0,v0,1
add v0,v1,a0
addiu v0,v0,60	//v offset=8*12
sb v0,fffb(t0)

addiu v0,r0,a	//short wh=10, write wh
sh v0,fffe(t0)
sh v0,0(t0)

lbu v0,1(a1)	//calculate clut.y by byte1, offset=472
nop
andi v0,v0,f
addi v0,v0,fffd
addiu v1,r0,01d8	
srl v0,v0,1
add v1,v1,v0
lw v0,0010(sp)		//load color, 1 is base color, if(color>1) clutYIncrement=0
nop
sltiu v0,v0,2
sll v0,v0,1
add v1,v1,v0
sll a0,v1,6

lbu v0,1(a1)	//calculate clut.x by byte1, offset=320
nop
andi v0,v0,f
addi v0,v0,fffd
addiu v1,r0,14	
andi v0,v0,1
add v1,v1,v0
add a0,a0,v1
sh a0,fffc(t0)

j 800d1840
addiu a1,a1,1