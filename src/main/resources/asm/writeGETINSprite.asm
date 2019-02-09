//function writeGetinSprite(int* a1_uv,)
//free used register:v0,v1,a0, limit ?? line
lbu v0,0(a1)	//load char encoding 2nd byte,calculate and write u
nop
andi v0,v0,f
sll v1,v0,3
sll a0,v0,1
add v0,v1,a0
sb v0,fffa(t0)

lbu v0,0(a1)  	//load char encoding 2nd byte,calculate and write v
nop
srl v0,v0,4
sll v1,v0,3
sll a0,v0,1
add v0,v1,a0
addiu v0,v0,60	//v offset=8*12
sb v0,fffb(t0)

lbu v0,1(a1)	//clut.y=474
nop
andi v0,v0,f
addi v0,v0,fffd
addiu v1,r0,01da	
srl v0,v0,1
add v1,v1,v0
sll a0,v1,6

lbu v0,1(a1)	//clut.x=320
nop
andi v0,v0,f
addi v0,v0,fffd
addiu v1,r0,14	
andi v0,v0,1
add v1,v1,v0
add a0,a0,v1
sh a0,fffc(t0)

addiu a1,a1,2	//uv pointer += 2
addiu t1,t1,14	//dma link head addr ++
sh t2,fffe(t0)	//write w
sh t2,0(t0)		//write h
addiu t0,t0,14
j 801785b4
nop