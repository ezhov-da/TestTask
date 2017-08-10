/*
* Copyright (c) 2017 Eugeny Dobrokvashin, All Rights Reserved.
*/

package ru.dobrokvashinevgeny.tander.accountability;

import org.junit.Test;

/**
 */
public class Test1 {
	@Test
	public void test() throws Exception {
		MaterialBuilderContext context = new MaterialBuilderContext();
		context.material(); System.out.println("Goto material level");
		context.uom(); System.out.println("Goto uom level");
		context.product(); System.out.println("Goto product level");
		context.fasetWine(); System.out.println("Goto fasetWine level");
//		context.supplier(); System.out.println("Goto supplier level");
	}
}

