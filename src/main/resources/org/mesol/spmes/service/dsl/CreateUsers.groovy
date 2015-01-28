def WC1 = userService {
    addGroup (
        UserGroup ([
            name: 'ROLE_ADMIN',
            users: [
                User ([
                    username: 'admin',
                    password: 'admin'
                ])
            ]
        ])
    )

    addGroup (
        UserGroup ([
            name: 'ROLE_LABORER',
            users: [
                User ([
                    username: 'admin',
                    password: 'admin'
                ])
            ]
        ])
    )
    
    addMenu (
        Menu ([
            name: 'Users',
            description: 'Users',
            url: 'users',
            parent: Menu([
                name: 'Security',
                description: 'Security',
                groups: [
                    UserGroup([
                        name: 'ROLE_ADMIN'
                    ]),
                    UserGroup([
                        name: 'ROLE_LABORER'
                    ])
                ]
            ])
        ])
    )
}

