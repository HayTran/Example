Swift is the result of the latest research on programming languages, combined with decades of experience building Apple platforms. Named parameters brought forward from Objective-C are expressed in a clean syntax that makes APIs in Swift even easier to read and maintain. Inferred types make code cleaner and less prone to mistakes, while modules eliminate headers and provide namespaces. Memory is managed automatically, and you don’t even need to type semi-colons. These forward-thinking concepts result in a language that is easy and fun to use.
extension String {
	var banana : String {
		let shortName = String(characters.dropFirst(1))
		return "\(self) \(self) Bo B\(shortName) Banana Fana Fo F\(shortName)"
	}
}

let bananaName = "Jimmy".banana		// "Jimmy Jimmy Bo Bimmy Banana Fana Fo Fimmy"
