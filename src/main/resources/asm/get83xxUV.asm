//function get83xxUV(int* a0_buildedMenuText, int* t0_uvSeedBaseAddr)
//in exe, limit 14 lines, free used register:v0,v1,a2,
address=8002336c
lbu v0,ffff(a0)	//read 1st byte again, save char code to v1
nop
add v1,v0,r0
sll v1,v1,8
lbu v0,0(a0)
nop
addi v0,v0,ffff
add v1,v1,v0
nop
nop
nop
nop
nop
nop