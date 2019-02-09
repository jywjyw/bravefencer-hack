//in exe, limit ? lines
address=800d1784
lh v1,0(a1)
addiu v0,r0,3870
andi v1,v1,ffff
beq v1,v0,800d17e4
addiu v0,r0,3871	//
beq v1,v0,800d17f0	//
slti v0,v1,6081		//
beq v0,r0,800d17b4
addiu v0,r0,1850
nop
j 800d1808
sh a2,fff6(t0)
nop
nop
addiu v0,r0,3872
beq v1,v0,800d17fc
nop
j 800d3c24			//if uv>8160, jump to writeMenuSprite.asm, else jump to old function
sh a2,fff6(t0)
