import type { Product } from './types.ts'

export const PRODUCTS: readonly Product[] = [
  { id: 'coffee-beans', name: 'Arabic coffee beans', description: '250 g, medium roast', price: 3.5 },
  { id: 'dates-box', name: 'Khalas dates box', description: '1 kg gift box', price: 6.25 },
  { id: 'saffron', name: 'Saffron threads', description: '2 g, grade A', price: 4.8 },
]

export const findProduct = (id: string): Product | undefined => PRODUCTS.find((product) => product.id === id)
