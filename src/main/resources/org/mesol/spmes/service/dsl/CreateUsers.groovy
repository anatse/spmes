def WC1 = userService {
    def adminUser = findByName ('admin')
    if (adminUser == null) {
        adminUser = save (
            User ([
               username: 'admin',
               password: 'admin'
        ]))
    }

    def adminGrp = addGroup (
        UserGroup ([
            name: 'ROLE_ADMIN',
            users: [
                adminUser
            ]
        ])
    )

    def laborerGrp = addGroup (
        UserGroup ([
            name: 'ROLE_LABORER',
            users: [
                adminUser
            ]
        ])
    )

    System.out.println ('saved...')
    
    def secMenu = addMenu (Menu([
        name: 'Security',
        description: 'Security',
        seq: 0,
        groups: [
            adminGrp,
            laborerGrp
        ]
    ]))
    
    addMenu (
        Menu ([
            name: 'Users',
            description: 'Users',
            seq: 0,
            url: 'users',
            parent: secMenu,
            groups: [
                adminGrp,
                laborerGrp
            ]
        ])
    )
    
    addMenu (
        Menu ([
            name: 'Equipment',
            description: 'Equipment',
            seq: 1,
            url: 'equipment',
            parent: secMenu,
            groups: [
                adminGrp,
                laborerGrp
            ]
        ])
    )
    
    addMenu (
        Menu ([
            name: 'Groups',
            description: 'Groups',
            seq: 2,
            url: 'groups',
            parent: secMenu,
            groups: [
                adminGrp,
                laborerGrp
            ]
        ])
    )
}

[
    success: true,
    error: ''
]